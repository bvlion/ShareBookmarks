package net.ambitious.android.sharebookmarks.ui.setting

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import net.ambitious.android.sharebookmarks.R

class SettingFragment : PreferenceFragmentCompat() {
  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences, rootKey)
  }
}