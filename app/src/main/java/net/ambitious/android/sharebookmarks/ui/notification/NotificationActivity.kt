package net.ambitious.android.sharebookmarks.ui.notification

import android.os.Bundle
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationActivity : BaseActivity() {

  private val viewModel by viewModel<NotificationViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    analyticsUtils.logStartActivity("NotificationActivity")

    setContentView(R.layout.activity_notification)
    setTitle(R.string.menu_notification)
  }
}