package net.ambitious.android.sharebookmarks.data.remote.etc

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EtcApi {
  @GET("etc/{lang}/terms_of_use")
  suspend fun getTermsOfUse(@Path("lang") lang: String): EtcEntity

  @GET("etc/{lang}/privacy_policy")
  suspend fun getPrivacyPolicy(@Path("lang") lang: String): EtcEntity

  @GET("etc/ogp")
  suspend fun getOgpImageUrl(@Query("url") url: String): OgpEntity

  @GET("etc/{lang}/faq")
  suspend fun getFaq(@Path("lang") lang: String): FaqEntity
}