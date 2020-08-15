package net.ambitious.android.sharebookmarks.ui.setting

import android.os.Bundle
import net.ambitious.android.sharebookmarks.ui.BaseActivity
import net.ambitious.android.sharebookmarks.R

class SettingActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    analyticsUtils.logStartActivity("SettingActivity")

    setContentView(R.layout.activity_setting)
    setTitle(R.string.menu_settings)
  }
}