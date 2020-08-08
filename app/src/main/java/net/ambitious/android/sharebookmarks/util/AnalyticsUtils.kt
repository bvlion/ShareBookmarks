package net.ambitious.android.sharebookmarks.util

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsUtils(private val fa: FirebaseAnalytics) {
  fun logStartActivity(className: String) =
    fa.logEvent("Activity", Bundle().apply {
      putString("class", className)
    })
}