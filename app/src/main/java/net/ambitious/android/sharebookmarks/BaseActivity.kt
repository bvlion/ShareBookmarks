package net.ambitious.android.sharebookmarks

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils
import net.ambitious.android.sharebookmarks.util.RemoteConfigUtils
import org.koin.android.ext.android.inject

open class BaseActivity : AppCompatActivity() {
  protected val analyticsUtils: AnalyticsUtils by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    RemoteConfigUtils.fetch()

//    setTheme(R.style.AppTheme_NoActionBar)

    if (isBackShowOnly()) {
      supportActionBar?.run {
        setDisplayHomeAsUpEnabled(true)
      }
    }
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