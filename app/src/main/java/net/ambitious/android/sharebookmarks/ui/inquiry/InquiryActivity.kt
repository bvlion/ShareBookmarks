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

    supportActionBar?.run {
      setDisplayHomeAsUpEnabled(true)
      setHomeAsUpIndicator(R.drawable.ic_clear_white)
    }
  }

  override fun finish() {
    super.finish()
    overridePendingTransition(android.R.anim.fade_in, R.anim.fade_out_bottom)
  }
}