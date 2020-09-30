package net.ambitious.android.sharebookmarks.ui.inquiry

import android.os.Bundle
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.ui.BaseActivity

class InquiryActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    analyticsUtils.logStartActivity("InquiryActivity")

    setContentView(R.layout.activity_inquiry)
    setTitle(R.string.other_menu_contact_us)
  }
}