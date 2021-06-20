package net.ambitious.android.sharebookmarks.util

import android.app.Activity
import android.os.Build
import android.util.DisplayMetrics

object DisplayCompat {
  fun getOutMetrics(activity: Activity?): DisplayMetrics {
    val outMetrics = DisplayMetrics()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      activity?.display?.getRealMetrics(outMetrics)
    } else {
      @Suppress("DEPRECATION")
      activity?.windowManager?.defaultDisplay?.getRealMetrics(outMetrics)
    }
    return outMetrics
  }
}