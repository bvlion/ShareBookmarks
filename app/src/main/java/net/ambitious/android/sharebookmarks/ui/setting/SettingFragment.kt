package net.ambitious.android.sharebookmarks.ui.setting

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.PreferencesUtils

class SettingFragment : PreferenceFragmentCompat(),
  SharedPreferences.OnSharedPreferenceChangeListener {

  var preferences: SharedPreferences? = null

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences, rootKey)

    context?.run {
      preferences =
        PreferenceManager.getDefaultSharedPreferences(context).apply {
          registerOnSharedPreferenceChangeListener(this@SettingFragment)
        }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    context?.run {
      preferences?.unregisterOnSharedPreferenceChangeListener(this@SettingFragment)
    }
  }

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    if (key == PreferencesUtils.START_FOLDER) {
      when (sharedPreferences?.getString(PreferencesUtils.START_FOLDER, "")) {
        Const.StartFolderType.ROOT.value ->
          sharedPreferences.edit().putLong(PreferencesUtils.START_FOLDER_ID, 0).apply()
        Const.StartFolderType.TARGET.value -> {
          sharedPreferences.edit().putLong(PreferencesUtils.START_FOLDER_ID, 0).apply()
          Snackbar.make(view ?: return, R.string.configurable_first_folder, Snackbar.LENGTH_LONG)
            .show()
        }
      }
    }
  }
}