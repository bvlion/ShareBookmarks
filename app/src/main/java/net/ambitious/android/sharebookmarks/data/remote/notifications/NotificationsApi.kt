package net.ambitious.android.sharebookmarks.data.remote.notifications

import retrofit2.http.GET

interface NotificationsApi {
  @GET("notifications/list")
  suspend fun getNotifications(): NotificationsEntity
}