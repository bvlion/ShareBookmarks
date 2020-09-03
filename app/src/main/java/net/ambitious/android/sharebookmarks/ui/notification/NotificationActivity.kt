package net.ambitious.android.sharebookmarks.ui.notification

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_notification.notification_message
import kotlinx.android.synthetic.main.activity_notification.notification_recycler_view
import kotlinx.android.synthetic.main.activity_notification.notification_refresh
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationActivity : BaseActivity(), NotificationListAdapter.OnNotificationClickListener {

  private val viewModel by viewModel<NotificationViewModel>()

  private lateinit var notificationListAdapter: NotificationListAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    analyticsUtils.logStartActivity("NotificationActivity")

    setContentView(R.layout.activity_notification)
    setTitle(R.string.menu_notification)
  }

  override fun onStart() {
    super.onStart()

    viewModel.notifications.observe(this, {
      notification_message.visibility = View.GONE
      notification_refresh.isRefreshing = false
      notificationListAdapter.setItems(it)
    })

    notificationListAdapter = NotificationListAdapter(this)
    notification_recycler_view.layoutManager = LinearLayoutManager(this)
    notification_recycler_view.adapter = notificationListAdapter

    notification_refresh.setOnRefreshListener {
      getNotifications()
    }
  }

  override fun onResume() {
    super.onResume()
    getNotifications()
  }

  override fun onRowClick(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
  }

  private fun getNotifications() {
    notification_refresh.isRefreshing = true
    viewModel.getNotifications()
  }
}