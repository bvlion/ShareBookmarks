package net.ambitious.android.sharebookmarks.ui.notification

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.databinding.ActivityNotificationBinding
import net.ambitious.android.sharebookmarks.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationActivity : BaseActivity(), NotificationListAdapter.OnNotificationClickListener {

  private val viewModel by viewModel<NotificationViewModel>()

  private lateinit var notificationListAdapter: NotificationListAdapter
  private lateinit var binding: ActivityNotificationBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    analyticsUtils.logStartActivity("NotificationActivity")
    binding = ActivityNotificationBinding.inflate(layoutInflater)

    setContentView(binding.root)
    setTitle(R.string.menu_notification)
  }

  override fun onStart() {
    super.onStart()

    viewModel.notifications.observe(this, {
      binding.loading.visibility = View.GONE
      binding.notificationRefresh.isRefreshing = false
      notificationListAdapter.setItems(it)
    })

    notificationListAdapter = NotificationListAdapter(this, analyticsUtils)
    binding.notificationRecyclerView.layoutManager = LinearLayoutManager(this)
    binding.notificationRecyclerView.adapter = notificationListAdapter

    binding.notificationRefresh.setOnRefreshListener {
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
    viewModel.getNotifications(preferences.userBearer != null)
  }
}