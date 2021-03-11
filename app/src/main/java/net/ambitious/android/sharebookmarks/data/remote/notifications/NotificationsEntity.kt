package net.ambitious.android.sharebookmarks.data.remote.notifications

import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class NotificationsEntity(val notifications: List<Notification>) {
  data class Notification(
    @Json(name = "target_date") val targetDate: String,
    val title: String,
    val subject: String,
    val url: String?
  )
}