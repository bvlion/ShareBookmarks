package net.ambitious.android.sharebookmarks.data.remote.etc

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EtcApi {
  @GET("etc/terms_of_use")
  suspend fun getTermsOfUse(): EtcEntity

  @GET("etc/privacy_policy")
  suspend fun getPrivacyPolicy(): EtcEntity

  @GET("etc/ogp")
  suspend fun getOgpImageUrl(@Query("url") url: String): OgpEntity

  @GET("etc/{lang}/faq")
  suspend fun getFaq(@Path("lang") lang: String): FaqEntity
}