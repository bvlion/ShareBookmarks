package net.ambitious.android.sharebookmarks.util

import android.content.Context

object PreferencesUtils {

  class Data(private val pref: Preference) {
    var userName: String?
      get() = pref.getString(USER_NAME)
      set(value) = pref.save(USER_NAME, value)

    var userEmail: String?
      get() = pref.getString(USER_EMAIL)
      set(value) = pref.save(USER_EMAIL, value)

    var userIcon: String?
      get() = pref.getString(USER_ICON)
      set(value) = pref.save(USER_ICON, value)

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
  }

  class Preference(private val context: Context) {
    fun save(saveKey: String, saveValue: String?) =
      context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
          .putString(saveKey, saveValue).apply()

    fun notSave() = Unit

    fun getString(saveKey: String) =
      context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getString(saveKey, null)

    fun getBoolean(saveKey: String) =
      context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean(saveKey, false)
  }

  private const val PREFERENCE_NAME = "sharebookmarks_preferences"

  // Google 情報
  private const val USER_NAME = "user_name"
  private const val USER_EMAIL = "user_email"
  private const val USER_ICON = "user_icon"

  // 設定
  private const val SHOW_MAIL_ADDRESS = "show_mail_address"
  private const val BACKUP_RESTORE_AUTO = "backup_restore_auto"
  private const val CLOSE_APP = "close_app"
  private const val START_FOLDER = "start_folder"
}