package net.ambitious.android.sharebookmarks.activity

import android.os.Bundle
import net.ambitious.android.sharebookmarks.BaseActivity
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils

class SettingActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AnalyticsUtils.logStartActivity(firebaseAnalytics, "SettingActivity")

    setContentView(R.layout.activity_setting)
    setTitle(R.string.menu_settings)
  }
}