package net.ambitious.android.sharebookmarks.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import net.ambitious.android.sharebookmarks.util.RemoteConfigUtils
import org.koin.android.ext.android.inject

open class BaseActivity : AppCompatActivity() {
  protected val analyticsUtils: AnalyticsUtils by inject()
  protected val preferences: PreferencesUtils.Data by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    RemoteConfigUtils.fetch()

    setTheme(R.style.IndigoTheme)

    if (isBackShowOnly()) {
      supportActionBar?.run {
        setDisplayHomeAsUpEnabled(true)
      }
    }

    FirebaseCrashlytics.getInstance().setUserId(preferences.userEmail ?: "none")
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home && isBackShowOnly()) {
      finish()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  open fun isBackShowOnly() = true
}