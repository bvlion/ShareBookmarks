package net.ambitious.android.sharebookmarks.ui.share

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.ui.BaseActivity

class ShareUserActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    analyticsUtils.logStartActivity("ShareUserActivity")

    setContentView(R.layout.activity_share)
    setTitle(R.string.share_title)

    supportActionBar?.run {
      setDisplayHomeAsUpEnabled(true)
      setHomeAsUpIndicator(R.drawable.ic_clear)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu) = true.apply {
    menuInflater.inflate(R.menu.share, menu)
  }

  override fun onPrepareOptionsMenu(menu: Menu?) = true.apply {
    menu?.let {
      it.findItem(R.id.menu_contact_permission).isVisible =
        Build.VERSION.SDK_INT > Build.VERSION_CODES.M
    }
  }

  override fun onOptionsItemSelected(item: MenuItem) = super.onOptionsItemSelected(item).apply {
    when (item.itemId) {
      android.R.id.home -> finish()
      R.id.menu_contact_permission -> {}
      R.id.menu_user_add_done -> {}
    }
  }

  override fun finish() {
    super.finish()
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
  }

  override fun isBackShowOnly() = false

  companion object {

    private const val PARAM_FOLDER_ID = "param_folder_id"

    fun createIntent(context: Context, folderId: Long) =
      Intent(context, ShareUserActivity::class.java).apply {
        putExtra(PARAM_FOLDER_ID, folderId)
      }
  }
}