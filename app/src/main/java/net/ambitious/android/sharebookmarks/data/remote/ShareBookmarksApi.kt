package net.ambitious.android.sharebookmarks.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.ambitious.android.sharebookmarks.BuildConfig
import net.ambitious.android.sharebookmarks.data.remote.contact.ContactApi
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class ShareBookmarksApi private constructor(private val client: OkHttpClient) {

  fun <T : Any> create(kClass: KClass<T>): T =
    Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_DOMAIN)
        .addConverterFactory(getFactory())
        .client(client)
        .build()
        .create(kClass.java)

  fun create4Inquiry(): ContactApi =
    Retrofit.Builder()
        .baseUrl("https://maker.ifttt.com/")
        .addConverterFactory(StringConverterFactory())
        .client(client)
        .build()
        .create(ContactApi::class.java)

  private fun getFactory() = MoshiConverterFactory.create(
      Moshi.Builder()
          .add(KotlinJsonAdapterFactory())
          .build()
  )

  companion object {
    fun createInstance(preferences: PreferencesUtils.Data) =
      ShareBookmarksApi(getClient(preferences))

    private fun getClient(preferences: PreferencesUtils.Data) =
      OkHttpClient
          .Builder()
          .connectTimeout(20, TimeUnit.SECONDS)
          .readTimeout(20, TimeUnit.SECONDS)
          .addInterceptor(AuthorizationInterceptor(preferences))
          .addInterceptor(
              HttpLoggingInterceptor().apply {
                level =
                  if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                  } else {
                    HttpLoggingInterceptor.Level.NONE
                  }
              }
          )
          .build()

    private class AuthorizationInterceptor(private val preferences: PreferencesUtils.Data) :
        Interceptor {
      override fun intercept(chain: Chain) = chain.proceed(
          chain.request().newBuilder().apply {
            preferences.userBearer?.let {
              header("Authorization", "Bearer $it")
            }
          }
              .method(chain.request().method, chain.request().body)
              .build()
      )
    }

    private class StringConverterFactory : Converter.Factory() {
      override fun responseBodyConverter(
        type: Type?, annotations: Array<Annotation?>?,
        retrofit: Retrofit?
      ): Converter<ResponseBody, String?> = Converter { it.string() }
    }
  }
}