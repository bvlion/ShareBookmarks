package net.ambitious.android.sharebookmarks.data.remote.users

import com.squareup.moshi.Json

object UsersEntity {
  data class UsersPostData(
    val email: String,
    val uid: String,
    @Json(name = "fcm_token") val fcmToken: String
  )

  data class AuthTokenResponse(
    val premium: Boolean,
    @Json(name = "access_token") val accessToken: String
  )
}