package net.ambitious.android.sharebookmarks.util

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsUtils {
  companion object {
    fun logStartActivity(fa: FirebaseAnalytics, className: String) =
      fa.logEvent("Activity", Bundle().apply {
        putString("class", className)
      })
  }
}