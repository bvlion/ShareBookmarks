package net.ambitious.android.sharebookmarks.ui.others

import android.os.Bundle
import android.view.MenuItem
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.ui.BaseActivity

class OtherActivity : BaseActivity() {

  private var state = 0

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
    }
    state = TOP
    setTitle(R.string.menu_other)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt(STATE_FRAGMENT, state)
  }

  fun termsOfUseShow() = true.apply {
    state = TERM
    supportFragmentManager.beginTransaction()
        .replace(R.id.nav_host_fragment, DetailFragment.newInstance(true))
        .addToBackStack(null)
        .commit()
    setTitle(R.string.other_menu_terms_of_use)
  }

  fun privacyPolicyShow() = true.apply {
    state = PRIVACY
    supportFragmentManager.beginTransaction()
        .replace(R.id.nav_host_fragment, DetailFragment.newInstance(false))
        .addToBackStack(null)
        .commit()
    setTitle(R.string.other_menu_privacy_policy)
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