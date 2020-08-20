package net.ambitious.android.sharebookmarks.ui.inquiry

import android.os.Bundle
import net.ambitious.android.sharebookmarks.ui.BaseActivity
import net.ambitious.android.sharebookmarks.R

class InquiryActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    analyticsUtils.logStartActivity("InquiryActivity")

    setContentView(R.layout.activity_inquiry)
    setTitle(R.string.menu_contact)
  }
}