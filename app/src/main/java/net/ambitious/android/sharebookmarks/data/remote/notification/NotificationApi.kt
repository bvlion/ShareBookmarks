package net.ambitious.android.sharebookmarks.data.remote.notification

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationApi {
  @GET("notifications")
  suspend fun getNotifications(): NotificationEntity

  @POST("notification/read/{id}")
  suspend fun postRead(
    @Path("id") notificationId: Long
  ): String
}