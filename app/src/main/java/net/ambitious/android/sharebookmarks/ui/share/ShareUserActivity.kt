package net.ambitious.android.sharebookmarks.ui.share

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.ui.BaseActivity

class ShareUserActivity : BaseActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    analyticsUtils.logStartActivity("ShareUserActivity")

    setContentView(R.layout.activity_share)
    setTitle(R.string.menu_contact)
  }

  override fun onCreateOptionsMenu(menu: Menu) = true.apply {
    menuInflater.inflate(R.menu.share, menu)
  }

  override fun finish() {
    super.finish()
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
  }

  companion object {

    private const val PARAM_FOLDER_ID = "param_folder_id"

    fun createIntent(context: Context, folderId: Long) =
      Intent(context, ShareUserActivity::class.java).apply {
        putExtra(PARAM_FOLDER_ID, folderId)
      }
  }
}