package net.ambitious.android.sharebookmarks.data.remote.notification

import com.squareup.moshi.Json

data class NotificationEntity(val notifications: List<Notification>) {
  data class Notification(
    val id: Long,
    @Json(name = "target_date") val targetDate: String,
    val title: String,
    val description: String,
    @Json(name = "transition_url") val transitionUrl: String,
    @Json(name = "is_read") val isRead: Boolean
  )
}