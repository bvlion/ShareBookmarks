package net.ambitious.android.sharebookmarks.data.source

interface ShareBookmarksDataSource {
  suspend fun syncAll()
  suspend fun syncShares()
  suspend fun syncItems(latestSync: String?)
}