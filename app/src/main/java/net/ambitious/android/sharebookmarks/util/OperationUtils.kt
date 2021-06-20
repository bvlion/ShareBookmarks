package net.ambitious.android.sharebookmarks.util

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.Data
import android.util.Base64
import net.ambitious.android.sharebookmarks.data.local.contact.Contact
import net.ambitious.android.sharebookmarks.data.remote.etc.EtcApi
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.jsoup.Jsoup
import java.net.URL
import java.util.regex.Pattern
import javax.net.ssl.HttpsURLConnection

object OperationUtils {
  private val DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

  fun datetimeFormat(time: Long?): String = DATETIME_FORMAT.print(time ?: DateTime().millis)

  fun datetimeParse(time: String): DateTime = DATETIME_FORMAT.parseDateTime(time)

  fun createThumbnailUrl(url: String?) = url?.let {
    getImageUrl(it)?.let { imageUrl ->
      Const.GOOGLE_FAVICON_URL + getImageUrl(imageUrl)!!.split("/")[2]
    }
  }

  @Suppress("BlockingMethodInNonBlockingContext")
  suspend fun getOgpImage(url: String, etcApi: EtcApi) = try {
    getImageUrl(url)?.let { processUrl ->
      Jsoup.connect(processUrl).get().select("meta[property~=og:image]")
        .map { it.attr("content") }.let { ogImage ->
          if (ogImage.isEmpty()) {
            null
          } else {
            if (isHttpStatusOk(ogImage.first())) {
              ogImage.first()
            } else {
              null
            }
          }
        }
    }
  } catch (e: Exception) {
    etcApi.getOgpImageUrl(url).url
  }

  private fun isHttpStatusOk(url: String) = try {
    val con = (URL(url).openConnection() as HttpsURLConnection).apply { requestMethod = "GET" }
    con.connect()
    con.responseCode == HttpsURLConnection.HTTP_OK
  } catch (_: Exception) {
    false
  }

  private fun getImageUrl(url: String) = url.let {
    Pattern.compile("https?://").matcher(url).find().let { isUrl ->
      if (isUrl) {
        url
      } else {
        it.split("://").let { schemePossibility ->
          if (schemePossibility.size < 2) {
            null
          } else {
            "https://${schemePossibility[0]}.com"
          }
        }
      }
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

  fun hiraganaToKatakana(str: String) =
    str.map {
      if (it.code in 0x3041..0x3093) {
        it + 0x60
      } else {
        it
      }
    }.joinToString("")

  fun kanaZenToHan(str: String) =
    hiraganaToKatakana(str)
      .map {
        if (it.code in ZEN[0].code..ZEN.last().code) {
          HAN[it.code - ZEN[0].code]
        } else {
          it
        }
      }.joinToString("")

  private val ZEN = listOf(
    'ァ', 'ア', 'ィ', 'イ', 'ゥ',
    'ウ', 'ェ', 'エ', 'ォ', 'オ', 'カ', 'ガ', 'キ', 'ギ', 'ク', 'グ', 'ケ', 'ゲ',
    'コ', 'ゴ', 'サ', 'ザ', 'シ', 'ジ', 'ス', 'ズ', 'セ', 'ゼ', 'ソ', 'ゾ', 'タ',
    'ダ', 'チ', 'ヂ', 'ッ', 'ツ', 'ヅ', 'テ', 'デ', 'ト', 'ド', 'ナ', 'ニ', 'ヌ',
    'ネ', 'ノ', 'ハ', 'バ', 'パ', 'ヒ', 'ビ', 'ピ', 'フ', 'ブ', 'プ', 'ヘ', 'ベ',
    'ペ', 'ホ', 'ボ', 'ポ', 'マ', 'ミ', 'ム', 'メ', 'モ', 'ャ', 'ヤ', 'ュ', 'ユ',
    'ョ', 'ヨ', 'ラ', 'リ', 'ル', 'レ', 'ロ', 'ヮ', 'ワ', 'ヰ', 'ヱ', 'ヲ', 'ン',
    'ヴ', 'ヵ', 'ヶ'
  )

  private val HAN = listOf(
    "ｧ", "ｱ", "ｨ", "ｲ", "ｩ",
    "ｳ", "ｪ", "ｴ", "ｫ", "ｵ", "ｶ", "ｶﾞ", "ｷ", "ｷﾞ", "ｸ", "ｸﾞ", "ｹ",
    "ｹﾞ", "ｺ", "ｺﾞ", "ｻ", "ｻﾞ", "ｼ", "ｼﾞ", "ｽ", "ｽﾞ", "ｾ", "ｾﾞ", "ｿ",
    "ｿﾞ", "ﾀ", "ﾀﾞ", "ﾁ", "ﾁﾞ", "ｯ", "ﾂ", "ﾂﾞ", "ﾃ", "ﾃﾞ", "ﾄ", "ﾄﾞ",
    "ﾅ", "ﾆ", "ﾇ", "ﾈ", "ﾉ", "ﾊ", "ﾊﾞ", "ﾊﾟ", "ﾋ", "ﾋﾞ", "ﾋﾟ", "ﾌ",
    "ﾌﾞ", "ﾌﾟ", "ﾍ", "ﾍﾞ", "ﾍﾟ", "ﾎ", "ﾎﾞ", "ﾎﾟ", "ﾏ", "ﾐ", "ﾑ", "ﾒ",
    "ﾓ", "ｬ", "ﾔ", "ｭ", "ﾕ", "ｮ", "ﾖ", "ﾗ", "ﾘ", "ﾙ", "ﾚ", "ﾛ", "ﾜ",
    "ﾜ", "ｲ", "ｴ", "ｦ", "ﾝ", "ｳﾞ", "ｶ", "ｹ"
  )

  private fun getUserImage(contentResolver: ContentResolver, contactId: Long) =
    Contacts.openContactPhotoInputStream(
      contentResolver,
      ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId)
    )?.let {
      Base64.encodeToString(it.readBytes(), Base64.DEFAULT)
    }
}