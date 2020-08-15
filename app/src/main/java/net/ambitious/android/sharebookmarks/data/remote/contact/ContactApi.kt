package net.ambitious.android.sharebookmarks.data.remote.contact

import net.ambitious.android.sharebookmarks.BuildConfig
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ContactApi {
  @FormUrlEncoded
  @POST(BuildConfig.CONTACT_URL)
  suspend fun postContact(
    @Field("value1") mail: String,
    @Field("value2") message: String
  ): String
}