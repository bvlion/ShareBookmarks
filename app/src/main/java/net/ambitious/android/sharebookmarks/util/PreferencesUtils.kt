package net.ambitious.android.sharebookmarks.util

import android.content.Context

object PreferencesUtils {

  class Data(private val pref: Preference) {
    var userName: String?
      get() = pref.get(USER_NAME)
      set(value) = pref.save(USER_NAME, value)

    var userEmail: String?
      get() = pref.get(USER_EMAIL)
      set(value) = pref.save(USER_EMAIL, value)

    var userIcon: String?
      get() = pref.get(USER_ICON)
      set(value) = pref.save(USER_ICON, value)
  }

  class Preference(private val context: Context) {
    fun save(saveKey: String, saveValue: String?) =
      context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
          .putString(saveKey, saveValue).apply()

    fun get(saveKey: String) =
      context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getString(saveKey, null)
  }

  private const val PREFERENCE_NAME = "sharebookmarks_preferences"

  private const val USER_NAME = "user_name"
  private const val USER_EMAIL = "user_email"
  private const val USER_ICON = "user_icon"
}