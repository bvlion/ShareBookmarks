package net.ambitious.android.sharebookmarks.ui.faq

import android.os.Bundle
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.ui.BaseActivity

class FaqActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    analyticsUtils.logStartActivity("OtherActivity")

    setContentView(R.layout.activity_setting)

    setTitle(R.string.questions_title)
  }
}