package net.ambitious.android.sharebookmarks.ui.others

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import net.ambitious.android.sharebookmarks.BuildConfig
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.ui.inquiry.InquiryActivity
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils
import org.koin.android.ext.android.inject

class OtherFragment : PreferenceFragmentCompat() {

  private val analyticsUtils: AnalyticsUtils by inject()

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.others, rootKey)

    val otherActivity = (activity as OtherActivity)

    preferenceScreen.findPreference<Preference>(VERSION)?.summary = BuildConfig.VERSION_NAME

    preferenceScreen.findPreference<Preference>(TERMS_OF_USE)?.setOnPreferenceClickListener {
      analyticsUtils.logOtherTap("Other", "term of use")
      otherActivity.termsOfUseShow()
    }

    preferenceScreen.findPreference<Preference>(PRIVACY_POLICY)?.setOnPreferenceClickListener {
      analyticsUtils.logOtherTap("Other", "privacy policy")
      otherActivity.privacyPolicyShow()
    }

    preferenceScreen.findPreference<Preference>(CONTACT_US)?.setOnPreferenceClickListener {
      analyticsUtils.logOtherTap("Other", "inquiry")
      startActivity(Intent(context, InquiryActivity::class.java))
      true
    }
  }

  companion object {
    const val TERMS_OF_USE = "terms_of_use"
    const val PRIVACY_POLICY = "privacy_policy"
    const val VERSION = "version"
    const val CONTACT_US = "contact_us"
  }
}