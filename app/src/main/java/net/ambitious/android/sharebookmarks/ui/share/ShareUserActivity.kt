package net.ambitious.android.sharebookmarks.ui.share

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.ui.BaseActivity
import net.ambitious.android.sharebookmarks.util.OperationUtils

class ShareUserActivity : BaseActivity() {

  private lateinit var shareUserFragment: ShareUserFragment
  private var isFromMenu = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    analyticsUtils.logStartActivity("ShareUserActivity")

    setContentView(R.layout.activity_share)
    setTitle(R.string.share_title)

    supportActionBar?.run {
      setDisplayHomeAsUpEnabled(true)
      setHomeAsUpIndicator(R.drawable.ic_clear_white)
    }
  }

  override fun onStart() {
    super.onStart()
    shareUserFragment =
      supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as ShareUserFragment
    shareUserFragment.setFolderId(intent.getLongExtra(PARAM_FOLDER_ID, 0))
    if (hasReadContactsPermission()) {
      sendContactData()
    } else {
      requestReadContactsPermission()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu) = true.apply {
    menuInflater.inflate(R.menu.share, menu)
  }

  override fun onPrepareOptionsMenu(menu: Menu?) = true.apply {
    menu?.let {
      it.findItem(R.id.menu_contact_permission).isVisible = !hasReadContactsPermission()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem) = super.onOptionsItemSelected(item).apply {
    when (item.itemId) {
      android.R.id.home -> {
        if (shareUserFragment.isChanged()) {
          AlertDialog.Builder(this@ShareUserActivity)
              .setMessage(R.string.share_end_warning)
              .setPositiveButton(R.string.share_end_warning_ok) { _, _ ->
                finish()
              }
              .setNegativeButton(R.string.dialog_cancel, null)
              .create().show()
        } else {
          finish()
        }
      }
      R.id.menu_contact_permission -> {
        isFromMenu = true
        requestReadContactsPermission()
      }
      R.id.menu_user_add_done -> {
        if (shareUserFragment.getInvalidMails().isNotEmpty()) {
          AlertDialog.Builder(this@ShareUserActivity)
              .setMessage(getString(R.string.share_email_invalid_dialog_message) + "\n\n" +
                  shareUserFragment.getInvalidMails().joinToString("\n") { it.userEmail })
              .setPositiveButton(R.string.share_done) { _, _ ->
                shareUserFragment.saveShare()
              }
              .setNegativeButton(R.string.dialog_cancel, null)
              .create().show()
        } else {
          shareUserFragment.saveShare()
        }
      }
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        sendContactData()
        invalidateOptionsMenu()
      } else {
        if (isFromMenu) {
          AlertDialog.Builder(this)
              .setMessage(R.string.share_permission_dialog)
              .setPositiveButton(android.R.string.ok, null)
              .create()
              .show()
        }
      }
    }
  }

  override fun finish() {
    super.finish()
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
  }

  override fun isBackShowOnly() = false

  private fun sendContactData() {
    shareUserFragment.setContactList(OperationUtils.getContactList(contentResolver))
  }

  private fun hasReadContactsPermission() =
    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
        PackageManager.PERMISSION_GRANTED

  private fun requestReadContactsPermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.READ_CONTACTS
        )
    ) {
      ActivityCompat.requestPermissions(
          this,
          arrayOf(Manifest.permission.READ_CONTACTS),
          PERMISSION_REQUEST_CODE
      )
    } else {
      if (isFromMenu) {
        AlertDialog.Builder(this)
            .setMessage(R.string.share_permission_disable_dialog_message)
            .setPositiveButton(R.string.share_permission_disable_dialog_ok) { d, _ ->
              d.dismiss()
              startActivity(
                  Intent(
                      Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                      Uri.parse("package:$packageName")
                  ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                  }
              )
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .create().show()
      }
    }
  }

  companion object {

    private const val PARAM_FOLDER_ID = "param_folder_id"
    private const val PERMISSION_REQUEST_CODE = 1005

    fun createIntent(context: Context, folderId: Long) =
      Intent(context, ShareUserActivity::class.java).apply {
        putExtra(PARAM_FOLDER_ID, folderId)
      }
  }
}