package net.ambitious.android.sharebookmarks.util

import android.content.Context
import androidx.preference.PreferenceManager

object PreferencesUtils {

  class Data(private val pref: Preference) {
    var userName: String?
      get() = if (pref.getString(SETTING_USER_NAME).isNullOrEmpty()) {
        pref.getString(USER_NAME)
      } else {
        pref.getString(SETTING_USER_NAME)
      }
      set(value) = pref.save(USER_NAME, value)

    var userEmail: String?
      get() = pref.getString(USER_EMAIL)
      set(value) = pref.save(USER_EMAIL, value)

    var userIcon: String?
      get() = pref.getString(USER_ICON)
      set(value) = pref.save(USER_ICON, value)

    var userBearer: String?
      get() = pref.getString(USER_BEARER)
      set(value) = pref.save(USER_BEARER, value)

    var showMailAddress: Boolean
      get() = pref.getBoolean(SHOW_MAIL_ADDRESS)
      set(_) = pref.notSave()

    var backupRestoreAuto: Boolean
      get() = pref.getBoolean(BACKUP_RESTORE_AUTO)
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
  }

  class Preference(private val context: Context) {
    fun save(saveKey: String, saveValue: String?) =
      PreferenceManager.getDefaultSharedPreferences(context).edit()
          .putString(saveKey, saveValue).apply()

    fun notSave() = Unit

    fun getString(saveKey: String) =
      PreferenceManager.getDefaultSharedPreferences(context).getString(saveKey, null)

    fun getBoolean(saveKey: String) =
      PreferenceManager.getDefaultSharedPreferences(context).getBoolean(saveKey, false)

    fun getStartFolderId() =
      PreferenceManager.getDefaultSharedPreferences(context).getLong(START_FOLDER_ID, 0)

    fun saveStartFolderId(id: Long) =
      PreferenceManager.getDefaultSharedPreferences(context).edit()
          .putLong(START_FOLDER_ID, id).apply()
  }

  // Google 情報
  private const val USER_NAME = "user_name"
  private const val USER_EMAIL = "user_email"
  private const val USER_ICON = "user_icon"
  private const val USER_BEARER = "user_bearer"

  // 設定
  private const val SETTING_USER_NAME = "setting_user_name"
  private const val SHOW_MAIL_ADDRESS = "show_mail_address"
  private const val BACKUP_RESTORE_AUTO = "backup_restore_auto"
  private const val CLOSE_APP = "close_app"
  const val START_FOLDER = "start_folder"
  const val START_FOLDER_ID = "start_folder_id"
}