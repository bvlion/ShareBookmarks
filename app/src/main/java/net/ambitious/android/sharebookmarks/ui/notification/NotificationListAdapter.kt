package net.ambitious.android.sharebookmarks.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.R.layout
import net.ambitious.android.sharebookmarks.data.remote.notifications.NotificationsEntity
import net.ambitious.android.sharebookmarks.data.remote.notifications.NotificationsEntity.Notification
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils

class NotificationListAdapter(
  private val listener: OnNotificationClickListener,
  private val analyticsUtils: AnalyticsUtils
) : Adapter<ViewHolder>() {

  private val _items = arrayListOf<Notification>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NotificationViewHolder(
      LayoutInflater.from(parent.context)
          .inflate(layout.row_notification, parent, false)
  )

  override fun getItemCount() = _items.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    (holder as NotificationViewHolder).apply {
      _items[position].let { notification ->
        titleTextView.text = notification.title
        subjectTextView.text = notification.subject
        dateTextView.text = notification.targetDate
        tileView.setOnClickListener {
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

  class NotificationViewHolder internal constructor(itemView: View) :
      ViewHolder(itemView) {
    val tileView = itemView.findViewById(R.id.row_notification) as LinearLayout
    val titleTextView = itemView.findViewById(R.id.title) as TextView
    val subjectTextView = itemView.findViewById(R.id.subject) as TextView
    val dateTextView = itemView.findViewById(R.id.date) as TextView
  }
}