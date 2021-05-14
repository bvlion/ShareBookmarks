package net.ambitious.android.sharebookmarks.ui.others

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import net.ambitious.android.sharebookmarks.BuildConfig
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.ui.faq.FaqActivity
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import org.koin.android.ext.android.inject

class OtherFragment : PreferenceFragmentCompat() {

  private val analyticsUtils: AnalyticsUtils by inject()
  private val preferences: PreferencesUtils.Data by inject()
  private var count = 0

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.others, rootKey)

    val otherActivity = (activity as OtherActivity)

    preferenceScreen.findPreference<Preference>(VERSION)?.summary = BuildConfig.VERSION_NAME
    preferenceScreen.findPreference<Preference>(VERSION)?.setOnPreferenceClickListener {
      context?.let {
        count++
        if (count == 10 && !preferences.userUid.isNullOrEmpty()) {
          AlertDialog.Builder(it)
            .setTitle(R.string.all_sync_dialog_title)
            .setMessage(R.string.all_sync_dialog_subject)
            .setPositiveButton(R.string.all_sync_dialog_exec) { _, _ ->
              activity?.setResult(AppCompatActivity.RESULT_OK)
              activity?.finish()
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .setCancelable(false)
            .create().show()
        }
      }
      true
    }

    preferenceScreen.findPreference<Preference>(TERMS_OF_USE)?.setOnPreferenceClickListener {
      analyticsUtils.logOtherTap("Other", "term of use")
      otherActivity.termsOfUseShow()
    }

    preferenceScreen.findPreference<Preference>(PRIVACY_POLICY)?.setOnPreferenceClickListener {
      analyticsUtils.logOtherTap("Other", "privacy policy")
      otherActivity.privacyPolicyShow()
    }

    preferenceScreen.findPreference<Preference>(QUESTIONS)?.setOnPreferenceClickListener {
      analyticsUtils.logOtherTap("Other", "inquiry")
      startActivity(Intent(context, FaqActivity::class.java))
      true
    }
  }

  companion object {
    const val TERMS_OF_USE = "terms_of_use"
    const val PRIVACY_POLICY = "privacy_policy"
    const val VERSION = "version"
    const val QUESTIONS = "questions"
  }
}