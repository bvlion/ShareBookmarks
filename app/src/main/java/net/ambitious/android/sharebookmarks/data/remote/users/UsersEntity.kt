package net.ambitious.android.sharebookmarks.data.remote.users

import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
object UsersEntity {
  @Keep
  data class UsersPostData(
    val email: String,
    val uid: String,
    @Json(name = "fcm_token") val fcmToken: String
  )

  @Keep
  data class AuthTokenResponse(
    val premium: Boolean,
    @Json(name = "access_token") val accessToken: String
  )
}