package net.ambitious.android.sharebookmarks.ui.setting

import android.os.Bundle
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.ui.BaseActivity

class SettingActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    analyticsUtils.logStartActivity("SettingActivity")

    setContentView(R.layout.activity_setting)
    setTitle(R.string.menu_settings)
  }

  override fun finish() {
    super.finish()
    overridePendingTransition(R.anim.slide_out_right, R.anim.slide_out_left)
  }
}