package net.ambitious.android.sharebookmarks

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import net.ambitious.android.sharebookmarks.util.RemoteConfigUtils

open class BaseActivity : AppCompatActivity() {
  protected lateinit var firebaseAnalytics: FirebaseAnalytics

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    RemoteConfigUtils.fetch()

//    setTheme(R.style.AppTheme_NoActionBar)

    if (isBackShowOnly()) {
      supportActionBar?.apply {
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