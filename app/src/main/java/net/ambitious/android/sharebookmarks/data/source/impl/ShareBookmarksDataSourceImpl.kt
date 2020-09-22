package net.ambitious.android.sharebookmarks.data.source.impl

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
import org.joda.time.DateTime

class ShareBookmarksDataSourceImpl(
  private val shareDao: ShareDao,
  private val itemDao: ItemDao,
  private val shareApi: ShareApi,
  private val itemApi: ItemApi
) : ShareBookmarksDataSource {
  override suspend fun dataUpdate() {
    updateItems()
    updateShares()
  }

  private suspend fun updateItems() {
    // 同期済みのローカルで削除した値をサーバーから削除
    itemApi.deleteItems(itemDao.getDeleteItems().map { ItemEntity.DeleteShare(it) })

    // サーバーとローカル全て取得する
    val remoteData = itemApi.getItems()
    val localData = itemDao.getAllItems()

    // ローカルに無い値を insert
    itemDao.insertAll(
        *remoteData.items
            .filter { share -> localData.none { it.remoteId == share.remoteId } }
            .map {
              Item(
                  null,
                  it.remoteId,
                  0,
                  it.name,
                  it.url,
                  it.orders,
                  it.ownerType,
                  1,
                  OperationUtils.datetimeParse(it.updated)
              )
            }
            .toTypedArray()
    )

    // サーバーに無い値を delete
    itemDao.delete(
        *localData
            .filter { it.remoteId != null }
            .filter { db -> remoteData.items.none { db.remoteId == it.remoteId } }
            .map { it.id!! }
            .toLongArray()
    )

    // 更新日時がサーバーの方が新しければローカルを更新
    localData
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
                      it.orders,
                      it.ownerType
                  )
              )
            }
          }
        }

    // フォルダ構成を再設定
    itemDao.getAllItems().forEach { db ->
      val remote = remoteData.items.firstOrNull { db.remoteId == it.remoteId }
      remote?.let {
        if (db.upserted.isBefore(OperationUtils.datetimeParse(it.updated)) ||
            db.upserted == OperationUtils.datetimeParse(it.updated)
        ) {
          itemDao.update(
              Item(
                  db.id,
                  db.remoteId,
                  itemDao.getSaveRemoteIdItem(it.parentId)?.id ?: 0,
                  db.name,
                  db.url,
                  db.order,
                  db.ownerType,
                  1,
                  OperationUtils.datetimeParse(it.updated)
              )
          )
        }
      }
    }

    // 共有配下は全て権限を同じにする
    itemDao.getShareFolders().forEach {
      updateOwnerType(it.ownerType, it.id!!)
    }

    // ソートを再構成してローカルを最新とする
    var beforeParentId = 0L
    var order = 0
    itemDao.getAllItems().forEach {
      if (beforeParentId != it.parentId) {
        beforeParentId = it.parentId
        order = 0
      }
      order++
      itemDao.update(
          Item(
              it.id,
              it.remoteId,
              it.parentId,
              it.name,
              it.url,
              order,
              it.ownerType,
              it.active,
              DateTime()
          )
      )
    }

    // ローカルの値をサーバーに送信
    itemApi.postItems(itemDao.getAllItems().map {
      ItemEntity.PostItem(
          it.id!!,
          it.remoteId,
          it.name,
          it.url,
          it.order,
          OperationUtils.datetimeFormat(it.upserted.millis)
      )
    }).items.forEach {
      itemDao.updateRemoteId(it.id, it.remoteId)
    }

    // ローカルで削除したデータはゴミなので物理削除
    itemDao.forceDelete()

    // サーバー側のフォルダの紐付きを更新
    itemApi.parentSetItems(itemDao.getAllItems().map {
      ItemEntity.ParentSet(
          it.remoteId!!,
          itemDao.getParentRemoteId(it.id!!) ?: 0,
          itemDao.getParentOwnerType(it.parentId) ?: 0 == OwnerType.OWNER.value && it.ownerType != OwnerType.OWNER.value
      )
    })
  }

  private suspend fun updateOwnerType(ownerType: Int, parentId: Long) {
    itemDao.updateOwnerType(ownerType, parentId)
    itemDao.getFolders(parentId).forEach {
      updateOwnerType(it.ownerType, it.id!!)
    }
  }

  private suspend fun updateShares() {
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
                  itemDao.getSaveRemoteIdItem(it.serverFolderId)?.id ?: 0,
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
                      itemDao.getSaveRemoteIdItem(it.serverFolderId)?.id ?: 0,
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