package net.ambitious.android.sharebookmarks.util

import android.content.ContentUris
import android.provider.ContactsContract

object OperationUtils {
  fun createThumbnailUrl(url: String?) =
    url?.split("://")?.let {
      if (it.size < 2) {
        null
      } else {
        Const.GOOGLE_FAVICON_URL + it[1].split("/")[0]
      }
    }

  fun getContactImageUri(id: Long) =
    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id)
}