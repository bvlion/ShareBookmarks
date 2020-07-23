package net.ambitious.android.sharebookmarks.util

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import net.ambitious.android.sharebookmarks.BuildConfig
import java.util.concurrent.TimeUnit

class RemoteConfigUtils {
  companion object {
    fun fetch() =
      FirebaseRemoteConfig.getInstance()
          .fetch(if (BuildConfig.DEBUG) 0 else CACHE_TIME_SECOND)
          .addOnCompleteListener { FirebaseRemoteConfig.getInstance().activate() }

    fun init() =
      FirebaseRemoteConfig.getInstance().setConfigSettingsAsync(
          FirebaseRemoteConfigSettings.Builder()
              .setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0 else CACHE_TIME_SECOND)
              .build()
      )

    private val CACHE_TIME_SECOND = TimeUnit.HOURS.toSeconds(1)
  }
}