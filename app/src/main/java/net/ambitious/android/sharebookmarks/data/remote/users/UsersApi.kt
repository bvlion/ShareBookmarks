package net.ambitious.android.sharebookmarks.data.remote.users

import net.ambitious.android.sharebookmarks.data.remote.users.UsersEntity.AuthTokenResponse
import net.ambitious.android.sharebookmarks.data.remote.users.UsersEntity.UsersPostData
import retrofit2.http.Body
import retrofit2.http.POST

interface UsersApi {
  @POST("users/auth")
  suspend fun userAuth(@Body usersDataPostEntity: UsersPostData): AuthTokenResponse
}