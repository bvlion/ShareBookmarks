package net.ambitious.android.sharebookmarks.util

import android.content.Context
import androidx.preference.PreferenceManager

object PreferencesUtils {

  class Data(private val pref: Preference) {
    var userName: String?
      get() = pref.getString(USER_NAME)
      set(value) = pref.save(USER_NAME, value)

    var userEmail: String?
      get() = pref.getString(USER_EMAIL)
      set(value) = pref.save(USER_EMAIL, value)

    var userUid: String?
      get() = pref.getString(USER_UID)
      set(value) = pref.save(USER_UID, value)

    var userIcon: String?
      get() = pref.getString(USER_ICON)
      set(value) = pref.save(USER_ICON, value)

    var userBearer: String?
      get() = pref.getString(USER_BEARER)
      set(value) = pref.save(USER_BEARER, value)

    var isPremium: Boolean
      get() = pref.getBoolean(USER_IS_PREMIUM)
      set(value) = pref.save(USER_IS_PREMIUM, value)

    var fcmToken: String?
      get() = pref.getString(FCM_TOKEN)
      set(value) = pref.save(FCM_TOKEN, value)

    var showMailAddress: Boolean
      get() = pref.getBooleanTrue(SHOW_MAIL_ADDRESS)
      set(_) = pref.notSave()

    var closeApp: Boolean
      get() = pref.getBoolean(CLOSE_APP)
      set(_) = pref.notSave()

    var startFolder: String?
      get() = pref.getString(START_FOLDER)
      set(_) = pref.notSave()

    var startFolderId: Long?
      get() = pref.getStartFolderId()
      set(value) = pref.saveStartFolderId(value ?: 0)

    var latestSync: String?
      get() = pref.getString(LATEST_ITEM_SYNC_DATE)
      set(value) = pref.save(LATEST_ITEM_SYNC_DATE, value)

    var shareSynced: Boolean
      get() = pref.getBoolean(SHARE_SYNCED)
      set(value) = pref.save(SHARE_SYNCED, value)

    var imageSyncTarget: String?
      get() = pref.getString(IMAGE_SYNC_TARGET)
      set(value) = pref.save(IMAGE_SYNC_TARGET, value)
  }

  class Preference(private val context: Context) {
    fun save(saveKey: String, saveValue: String?) =
      PreferenceManager.getDefaultSharedPreferences(context).edit()
        .putString(saveKey, saveValue).apply()

    fun save(saveKey: String, saveValue: Boolean) =
      PreferenceManager.getDefaultSharedPreferences(context).edit()
        .putBoolean(saveKey, saveValue).apply()

    fun notSave() = Unit

    fun getString(saveKey: String) =
      PreferenceManager.getDefaultSharedPreferences(context).getString(saveKey, null)

    fun getBooleanTrue(saveKey: String) =
      PreferenceManager.getDefaultSharedPreferences(context).getBoolean(saveKey, true)

    fun getBoolean(saveKey: String) =
      PreferenceManager.getDefaultSharedPreferences(context).getBoolean(saveKey, false)

    fun getStartFolderId() =
      PreferenceManager.getDefaultSharedPreferences(context).getLong(START_FOLDER_ID, 0)

    fun saveStartFolderId(id: Long) =
      PreferenceManager.getDefaultSharedPreferences(context).edit()
        .putLong(START_FOLDER_ID, id).apply()
  }

  // ユーザー情報
  private const val USER_NAME = "user_name"
  private const val USER_EMAIL = "user_email"
  private const val USER_UID = "user_uid"
  private const val USER_ICON = "user_icon"
  private const val USER_BEARER = "user_bearer"
  private const val USER_IS_PREMIUM = "user_is_premium"
  private const val FCM_TOKEN = "fcm_token"

  // 同期
  private const val LATEST_ITEM_SYNC_DATE = "latest_item_sync_date"
  private const val SHARE_SYNCED = "share_synced"
  private const val IMAGE_SYNC_TARGET = "image_sync_target"

  // 設定
  private const val SHOW_MAIL_ADDRESS = "show_mail_address"
  private const val CLOSE_APP = "close_app"
  const val START_FOLDER = "start_folder"
  const val START_FOLDER_ID = "start_folder_id"
}