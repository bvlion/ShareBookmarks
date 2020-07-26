package net.ambitious.android.sharebookmarks.ui.admob

import android.os.Bundle
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

class AdmobFragment : Fragment() {

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
    admobView = AdView(context).apply {
      adSize = AdSize.BANNER
      adUnitId = BuildConfig.AD_MOB_BANNER_KEY
    }
    admobContainer?.addView(admobView)
    admobView?.loadAd(AdRequest.Builder().build())
  }

  override fun onDestroy() {
    admobContainer?.let {
      admobView.run {
        it.removeView(this)
      }
    }
    admobContainer = null
    super.onDestroy()
  }
}