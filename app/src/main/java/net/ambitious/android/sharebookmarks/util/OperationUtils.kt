package net.ambitious.android.sharebookmarks.util

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.Data
import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.ambitious.android.sharebookmarks.data.local.contact.Contact
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.jsoup.Jsoup
import java.util.regex.Pattern

object OperationUtils {
  private val DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

  fun datetimeFormat(time: Long?): String = DATETIME_FORMAT.print(time ?: DateTime().millis)

  fun datetimeParse(time: String): DateTime = DATETIME_FORMAT.parseDateTime(time)

  suspend fun createThumbnailUrl(url: String?) = url?.let {
    Pattern.compile("https?://").matcher(url).find().let { isUrl ->
      if (isUrl) {
        getOgpImage(it)
      } else {
        it.split("://").let { schemePossibility ->
          if (schemePossibility.size < 2) {
            null
          } else {
            getOgpImage("https://${schemePossibility[0]}.com")
          }
        }
      }
    }
  }

  @Suppress("BlockingMethodInNonBlockingContext")
  private suspend fun getOgpImage(url: String) =
    Jsoup.connect(url).get().select("meta[property~=og:image]")
        .map { it.attr("content") }.let { ogImage ->
          if (ogImage.isEmpty()) {
            Const.GOOGLE_FAVICON_URL + url.split("/")[2]
          } else {
            ogImage.first()
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