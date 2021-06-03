package net.ambitious.android.sharebookmarks.ui.notification

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.databinding.ActivityApiListViewBinding
import net.ambitious.android.sharebookmarks.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationActivity : BaseActivity(), NotificationListAdapter.OnNotificationClickListener {

  private val viewModel by viewModel<NotificationViewModel>()

  private lateinit var notificationListAdapter: NotificationListAdapter
  private lateinit var binding: ActivityApiListViewBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    analyticsUtils.logStartActivity("NotificationActivity")
    binding = ActivityApiListViewBinding.inflate(layoutInflater)

    setContentView(binding.root)
    setTitle(R.string.menu_notification)
  }

  override fun onStart() {
    super.onStart()

    viewModel.notifications.observe(this, {
      binding.loading.visibility = View.GONE
      binding.refresh.isRefreshing = false
      binding.errorText.isVisible = it.notifications.isEmpty()
      notificationListAdapter.setItems(it)
    })

    notificationListAdapter = NotificationListAdapter(this, analyticsUtils)
    binding.recyclerView.layoutManager = LinearLayoutManager(this)
    binding.recyclerView.adapter = notificationListAdapter

    binding.refresh.setOnRefreshListener {
      viewModel.refresh()
    }
  }

  override fun finish() {
    super.finish()
    overridePendingTransition(R.anim.slide_out_right, R.anim.slide_out_left)
  }

  override fun onRowClick(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
  }
}