package net.ambitious.android.sharebookmarks.util

object OperationUtils {
  fun createThumbnailUrl(url: String?) =
    url?.split("://")?.let {
      if (it.size < 2) {
        null
      } else {
        Const.GOOGLE_FAVICON_URL + it[1].split("/")[0]
      }
    }
}