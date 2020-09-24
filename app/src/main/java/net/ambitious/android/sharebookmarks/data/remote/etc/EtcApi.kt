package net.ambitious.android.sharebookmarks.data.remote.etc

import retrofit2.http.GET
import retrofit2.http.Path

interface EtcApi {
  @GET("etc/{lang}/terms_of_use")
  suspend fun getTermsOfUse(@Path("lang") lang: String): EtcEntity

  @GET("etc/{lang}/privacy_policy")
  suspend fun getPrivacyPolicy(@Path("lang") lang: String): EtcEntity
}