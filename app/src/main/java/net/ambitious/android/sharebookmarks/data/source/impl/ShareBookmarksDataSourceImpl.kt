package net.ambitious.android.sharebookmarks.data.source.impl

import net.ambitious.android.sharebookmarks.data.local.item.ItemDao
import net.ambitious.android.sharebookmarks.data.local.share.ShareDao
import net.ambitious.android.sharebookmarks.data.remote.item.ItemApi
import net.ambitious.android.sharebookmarks.data.remote.share.ShareApi
import net.ambitious.android.sharebookmarks.data.source.ShareBookmarksDataSource

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

  private fun updateItems() {
    // サーバーとローカル全て取得する

    // 更新日時がサーバーの方が新しければローカルを更新
    // ローカルに無い値を insert
    // サーバーに無い値を delete

    // ローカルにしかない値とローカルの方が新しい値をサーバーに送信

    // ローカルで削除した値をサーバーから削除

    // サーバー側のフォルダの紐付きを更新
  }

  private fun updateShares() {
    // サーバーとローカル全て取得する

    // 更新日時がサーバーの方が新しければローカルを更新
    // ローカルに無い値を insert
    // サーバーに無い値を delete

    // ローカルにしかない値とローカルの方が新しい値をサーバーに送信

    // ローカルで削除した値をサーバーから削除
  }
}