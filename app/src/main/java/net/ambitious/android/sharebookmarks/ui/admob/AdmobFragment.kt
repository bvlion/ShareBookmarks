package net.ambitious.android.sharebookmarks.ui.admob

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import net.ambitious.android.sharebookmarks.BuildConfig
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import org.koin.android.ext.android.inject

class AdmobFragment : Fragment() {

  private val preferences: PreferencesUtils.Data by inject()

  private var admobContainer: LinearLayout? = null
  private var admobView: AdView? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_admob, container, false).apply {
    admobContainer = findViewById(R.id.admob_container)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    if (!preferences.isPremium) {
      showAdmobBanner()
    }
  }

  fun displayChange() {
    if (preferences.isPremium) {
      hideAdmobBanner()
    } else {
      showAdmobBanner()
    }
  }

  override fun onPause() {
    admobView?.pause()
    super.onPause()
  }

  override fun onResume() {
    super.onResume()
    admobView?.resume()
  }

  override fun onDestroy() {
    hideAdmobBanner()
    admobContainer = null
    super.onDestroy()
  }

  private fun showAdmobBanner() {
    admobView = AdView(context).apply {
      adSize = getAdaptiveAdSize()
      adUnitId = BuildConfig.AD_MOB_BANNER_KEY
    }
    admobContainer?.addView(admobView)
    admobView?.loadAd(AdRequest.Builder().build())
  }

  private fun hideAdmobBanner() {
    admobContainer?.let {
      admobView?.run {
        it.removeView(this)
        destroy()
      }
    }
  }

  private fun getAdaptiveAdSize(): AdSize {
    val display = activity?.windowManager?.defaultDisplay
    val outMetrics = DisplayMetrics()
    display?.getMetrics(outMetrics)

    val density = outMetrics.density

    var adWidthPixels = admobContainer?.width?.toFloat()
    if (adWidthPixels == 0f) {
      adWidthPixels = outMetrics.widthPixels.toFloat()
    }

    val adWidth = (adWidthPixels?.div(density))?.toInt()
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
        context,
        adWidth ?: return AdSize.BANNER
    )
  }
}