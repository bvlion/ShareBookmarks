package net.ambitious.android.sharebookmarks.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.ambitious.android.sharebookmarks.data.remote.notifications.NotificationsEntity
import net.ambitious.android.sharebookmarks.data.remote.notifications.NotificationsEntity.Notification
import net.ambitious.android.sharebookmarks.databinding.RowNotificationBinding
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils

class NotificationListAdapter(
  private val listener: OnNotificationClickListener,
  private val analyticsUtils: AnalyticsUtils
) : Adapter<ViewHolder>() {

  private val _items = arrayListOf<Notification>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NotificationViewHolder(
    RowNotificationBinding.inflate(
      LayoutInflater.from(parent.context), parent, false
    )
  )

  override fun getItemCount() = _items.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    (holder as NotificationViewHolder).apply {
      _items[position].let { notification ->
        row.notification = notification
        row.rowNotification.setOnClickListener {
          analyticsUtils.logOtherTap("notification", "onRowClick ${notification.url}")
          notification.url?.let {
            listener.onRowClick(it)
          }
        }
      }
    }
  }

  interface OnNotificationClickListener {
    fun onRowClick(url: String)
  }

  fun setItems(entity: NotificationsEntity) {
    _items.clear()
    _items.addAll(entity.notifications)
    notifyDataSetChanged()
  }

  class NotificationViewHolder(val row: RowNotificationBinding) : ViewHolder(row.root)
}