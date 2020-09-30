package net.ambitious.android.sharebookmarks.data.source

import kotlinx.coroutines.CoroutineScope

interface ShareBookmarksDataSource {
  suspend fun dataUpdate(coroutineScope: CoroutineScope)
}