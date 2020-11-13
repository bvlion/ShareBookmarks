package net.ambitious.android.sharebookmarks.data.source.impl

import com.squareup.moshi.Moshi
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.data.local.item.ItemDao
import net.ambitious.android.sharebookmarks.data.local.share.Share
import net.ambitious.android.sharebookmarks.data.local.share.ShareDao
import net.ambitious.android.sharebookmarks.data.remote.item.ItemApi
import net.ambitious.android.sharebookmarks.data.remote.item.ItemEntity
import net.ambitious.android.sharebookmarks.data.remote.share.ShareApi
import net.ambitious.android.sharebookmarks.data.remote.share.ShareEntity
import net.ambitious.android.sharebookmarks.data.source.ShareBookmarksDataSource
import net.ambitious.android.sharebookmarks.util.Const.OwnerType
import net.ambitious.android.sharebookmarks.util.OperationUtils
import net.ambitious.android.sharebookmarks.util.PreferencesUtils

class ShareBookmarksDataSourceImpl(
  private val shareDao: ShareDao,
  private val itemDao: ItemDao,
  private val shareApi: ShareApi,
  private val itemApi: ItemApi,
  private val preferences: PreferencesUtils.Data
) : ShareBookmarksDataSource {

  override suspend fun syncAll() {
    syncItems(null)
    syncShares()
  }

  override suspend fun syncItems(latestSync: String?) {
    // 同期済みのローカルで削除した値をサーバーから削除
    itemDao.getDeleteItems().map { ItemEntity.DeleteShare(it) }.let {
      if (it.isNotEmpty()) {
        itemApi.deleteItems(it)
      }
    }

    // 新規の場合はローカルの値を全て削除
    if (latestSync == null) {
      itemDao.deleteAllItems()
    }

    // サーバーとローカルの対象データを取得する
    val remoteData = itemApi.getItems(latestSync)
    val localAll = itemDao.getAllItems()

    // 画像同期対象リスト
    val imageUpdates = mutableMapOf<Long, String>()

    // ローカルに無い値を insert
    remoteData.items
        .filter { share -> localAll.none { it.remoteId == share.remoteId } }
        .map {
          Item(
              null,
              it.remoteId,
              0,
              it.name,
              it.url,
              null,
              it.orders,
              it.ownerType,
              if (it.deleted) 0 else 1,
              OperationUtils.datetimeParse(it.updated)
          )
        }
        .toTypedArray().let {
          if (it.isNotEmpty()) {
            itemDao.insertAll(*it)
          }
          if (latestSync != null) {
            it.map { item ->
              item.url?.let { url ->
                imageUpdates[itemDao.getLocalIdFromRemoteId(item.remoteId!!)!!] = url
              }
            }
          }
        }

    // 更新日時がサーバーの方が新しければローカルを更新
    localAll
        .filter { it.remoteId != null }
        .forEach { db ->
          val remote = remoteData.items.firstOrNull { db.remoteId == it.remoteId }
          remote?.let {
            if (db.upserted.isBefore(OperationUtils.datetimeParse(it.updated))) {
              itemDao.update(
                  Item(
                      db.id,
                      it.remoteId,
                      db.parentId,
                      it.name,
                      it.url,
                      db.ogpUrl,
                      it.orders,
                      it.ownerType,
                      if (it.deleted) 0 else 1,
                      OperationUtils.datetimeParse(it.updated)
                  )
              )
            }
          }
        }

    // フォルダ構成を再設定
    itemDao.getAllItems().forEach { db ->
      val remote = remoteData.items.firstOrNull { db.remoteId == it.remoteId }
      remote?.let {
        itemDao.update(
            Item(
                db.id,
                db.remoteId,
                itemDao.getLocalIdFromRemoteId(it.remoteParentId) ?: 0,
                db.name,
                db.url,
                db.ogpUrl,
                db.order,
                db.ownerType,
                db.active,
                db.upserted
            )
        )
      }
    }

    // 共有配下は全て権限を同じにする
    itemDao.getShareFolders().forEach {
      updateOwnerType(it.ownerType, it.id!!)
    }

    // ローカルの値をサーバーに送信
    if (latestSync != null) {
      getLocalItems(latestSync).map {
        ItemEntity.PostItem(
            it.id!!,
            it.remoteId,
            it.name,
            it.url,
            it.order,
            OperationUtils.datetimeFormat(it.upserted.millis)
        )
      }.let {
        if (it.isNotEmpty()) {
          itemApi.postItems(it).items.forEach { item ->
            itemDao.updateRemoteId(item.id, item.remoteId)
          }
        }
      }

      if (imageUpdates.isNotEmpty()) {
        preferences.imageSyncTarget =
          Moshi.Builder().build().adapter(Map::class.java).toJson(imageUpdates)
      }
    }

    // サーバー側のフォルダの紐付きを更新
    getLocalItems(latestSync).map {
      ItemEntity.ParentSet(
          it.remoteId!!,
          itemDao.getParentRemoteId(it.id!!) ?: 0,
          itemDao.getParentOwnerType(it.parentId) ?: 0 == OwnerType.OWNER.value && it.ownerType != OwnerType.OWNER.value
      )
    }.let {
      if (it.isNotEmpty()) {
        itemApi.parentSetItems(it)
      }
    }

    // ローカルで削除したデータはゴミなので物理削除
    itemDao.forceDelete()
  }

  private suspend fun getLocalItems(latestSync: String?) =
    latestSync?.let {
      itemDao.getTargetItems(OperationUtils.datetimeParse(it))
    } ?: itemDao.getAllItems()

  private suspend fun updateOwnerType(ownerType: Int, parentId: Long) {
    itemDao.updateOwnerType(ownerType, parentId)
    itemDao.getFolders(parentId).forEach {
      updateOwnerType(it.ownerType, it.id!!)
    }
  }

  override suspend fun syncShares() {
    // 同期済みのローカルで削除した値をサーバーから削除
    shareApi.deleteShares(shareDao.getDeleteShares().map { ShareEntity.DeleteShare(it) })

    // サーバーとローカル全て取得する
    val remoteData = shareApi.getShares()
    val localData = shareDao.getAllShares()

    // ローカルに無い値を insert
    shareDao.insertAll(
        *remoteData.shares
            .filter { share -> localData.none { it.remoteId == share.remoteId } }
            .map {
              Share(
                  null,
                  it.remoteId,
                  itemDao.getLocalIdFromRemoteId(it.serverFolderId) ?: 0,
                  it.userEmail,
                  null,
                  null,
                  it.ownerType,
                  1,
                  OperationUtils.datetimeParse(it.updated)
              )
            }
            .toTypedArray()
    )

    // サーバーに無い値を delete
    shareDao.delete(
        *localData
            .filter { it.remoteId != null }
            .filter { db -> remoteData.shares.none { db.remoteId == it.remoteId } }
            .map { it.id!! }
            .toLongArray()
    )

    // 更新日時がサーバーの方が新しければローカルを更新
    localData
        .filter { it.remoteId != null }
        .forEach { db ->
          val remote = remoteData.shares.firstOrNull { db.remoteId == it.remoteId }
          remote?.let {
            if (db.upserted.isBefore(OperationUtils.datetimeParse(it.updated))) {
              shareDao.update(
                  Share(
                      db.id,
                      it.remoteId,
                      itemDao.getLocalIdFromRemoteId(it.serverFolderId) ?: 0,
                      it.userEmail,
                      db.userName,
                      db.userIcon,
                      it.ownerType,
                      1,
                      OperationUtils.datetimeParse(it.updated)
                  )
              )
            }
          }
        }

    // ローカルの値をサーバーに送信
    shareApi.postShares(shareDao.getAllShares().map {
      ShareEntity.PostShare(
          it.id!!,
          it.remoteId,
          itemDao.getItem(it.folderId)?.remoteId ?: 0,
          it.userEmail,
          it.ownerType,
          OperationUtils.datetimeFormat(it.upserted.millis)
      )
    }).shares.forEach {
      shareDao.updateRemoteId(it.id, it.remoteId)
    }

    // ローカルで削除したデータはゴミなので物理削除
    shareDao.forceDelete()
  }
}