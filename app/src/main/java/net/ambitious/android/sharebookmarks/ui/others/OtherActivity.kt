package net.ambitious.android.sharebookmarks.ui.others

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.ui.BaseActivity

class OtherActivity : BaseActivity() {

  private var state = 0

  private val handler = Handler(Looper.getMainLooper())

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    analyticsUtils.logStartActivity("OtherActivity")

    setContentView(R.layout.activity_setting)

    supportFragmentManager
      .beginTransaction()
      .replace(R.id.nav_host_fragment, OtherFragment())
      .commit()
    setTitle(R.string.menu_other)

    savedInstanceState?.let {
      state = it.getInt(STATE_FRAGMENT)
    }

    when (state) {
      TERM -> termsOfUseShow()
      PRIVACY -> privacyPolicyShow()
    }
  }

  override fun onBackPressed() {
    super.onBackPressed()
    if (state == TOP) {
      finish()
      return
    }
    state = TOP
    handler.postDelayed({
      setTitle(R.string.menu_other)
      supportActionBar?.run { setHomeAsUpIndicator(null) }
    }, 200)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt(STATE_FRAGMENT, state)
  }

  override fun finish() {
    super.finish()
    overridePendingTransition(R.anim.slide_out_right, R.anim.slide_out_left)
  }

  fun termsOfUseShow() = true.apply {
    state = TERM
    supportActionBar?.run {
      setDisplayHomeAsUpEnabled(true)
      setHomeAsUpIndicator(R.drawable.ic_clear_white)
    }
    supportFragmentManager.beginTransaction()
      .setCustomAnimations(R.anim.fade_in_top, android.R.anim.fade_out, android.R.anim.fade_in, R.anim.fade_out_bottom_fragment)
      .replace(R.id.nav_host_fragment, DetailFragment.newInstance(true))
      .addToBackStack(null)
      .commit()
  }

  fun privacyPolicyShow() = true.apply {
    state = PRIVACY
    supportActionBar?.run {
      setDisplayHomeAsUpEnabled(true)
      setHomeAsUpIndicator(R.drawable.ic_clear_white)
    }
    supportFragmentManager.beginTransaction()
      .setCustomAnimations(R.anim.fade_in_top, android.R.anim.fade_out, android.R.anim.fade_in, R.anim.fade_out_bottom_fragment)
      .replace(R.id.nav_host_fragment, DetailFragment.newInstance(false))
      .addToBackStack(null)
      .commit()
  }

  override fun onOptionsItemSelected(item: MenuItem) =
    if (item.itemId == android.R.id.home) {
      onBackPressed()
      false
    } else {
      super.onOptionsItemSelected(item)
    }

  companion object {
    const val STATE_FRAGMENT = "state_fragment"

    const val TOP = 0
    const val TERM = 1
    const val PRIVACY = 2
  }
}