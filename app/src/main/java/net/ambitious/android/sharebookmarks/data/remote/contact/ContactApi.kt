package net.ambitious.android.sharebookmarks.data.remote.contact

import net.ambitious.android.sharebookmarks.BuildConfig
import retrofit2.http.Body
import retrofit2.http.POST

interface ContactApi {
  @POST(BuildConfig.CONTACT_URL)
  suspend fun postContact(@Body contact: ContactEntity): Any
}