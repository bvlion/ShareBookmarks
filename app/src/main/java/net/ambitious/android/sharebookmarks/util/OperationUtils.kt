package net.ambitious.android.sharebookmarks.util

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.Data
import android.util.Base64
import net.ambitious.android.sharebookmarks.data.local.contact.Contact

object OperationUtils {
  fun createThumbnailUrl(url: String?) =
    url?.split("://")?.let {
      if (it.size < 2) {
        null
      } else {
        Const.GOOGLE_FAVICON_URL + it[1].split("/")[0]
      }
    }

  fun getContactList(contentResolver: ContentResolver): List<Contact> {
    val emailMap = hashMapOf<String, String>()
    contentResolver.query(Email.CONTENT_URI, null, null, null, null).use {
      while (it?.moveToNext() == true) {
        emailMap[it.getString(it.getColumnIndex(Email._ID))] =
          it.getString(it.getColumnIndex(Email.DATA1))
      }
    }

    val contactList = arrayListOf<Contact>()
    contentResolver.query(Data.CONTENT_URI, null, null, null, null).use {
      while (it?.moveToNext() == true) {
        emailMap[it.getString(it.getColumnIndex(Data._ID))]?.let { email ->
          contactList.add(
              Contact(
                  it.getString(it.getColumnIndex(Data._ID)),
                  email,
                  it.getString(it.getColumnIndex(Data.DISPLAY_NAME)),
                  getUserImage(
                      contentResolver,
                      it.getString(it.getColumnIndex(Data.CONTACT_ID)).toLong()
                  )
              )
          )
        }
      }
    }
    return contactList
  }

  private fun getUserImage(contentResolver: ContentResolver, contactId: Long) =
    Contacts.openContactPhotoInputStream(
        contentResolver,
        ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId)
    )?.let {
      Base64.encodeToString(it.readBytes(), Base64.DEFAULT)
    }
}