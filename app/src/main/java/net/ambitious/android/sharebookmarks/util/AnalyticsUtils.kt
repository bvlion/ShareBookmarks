package net.ambitious.android.sharebookmarks.util

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsUtils(private val fa: FirebaseAnalytics) {

  fun logStartActivity(className: String) =
    fa.logEvent("Activity", Bundle().apply {
      putString("class", className)
    })

  fun logHomeTap(tapPoint: String, param: String = "none") =
    fa.logEvent("HomeTap", Bundle().apply {
      putString(tapPoint, param)
    })

  fun logMenuTap(tapPoint: String) =
    fa.logEvent("Menu", Bundle().apply {
      putString("tap", tapPoint)
    })

  fun logDataUpdateTime(time: Long) =
    fa.logEvent("dataUpdate", Bundle().apply {
      putString("total", "$time msec")
    })

  fun logOtherTap(page: String, tapPoint: String) =
    fa.logEvent("OtherTap", Bundle().apply {
      putString(page, tapPoint)
    })

  fun logResult(action: String, result: String) =
    fa.logEvent("Result", Bundle().apply {
      putString(action, result)
    })
}