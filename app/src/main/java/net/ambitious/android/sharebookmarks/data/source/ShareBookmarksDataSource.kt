package net.ambitious.android.sharebookmarks.data.source

interface ShareBookmarksDataSource {
  suspend fun dataUpdate()
}