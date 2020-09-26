package net.ambitious.android.sharebookmarks.ui.shareadd

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.ui.ItemEditDialogFragment
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShareAddActivity : AppCompatActivity(), ItemEditDialogFragment.OnClickListener {

  private val analyticsUtils: AnalyticsUtils by inject()
  private val viewModel by viewModel<ShareAddViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    analyticsUtils.logStartActivity("ShareAddActivity")
    if (intent?.action != Intent.ACTION_SEND || intent.type != "text/plain") {
      finish()
      return
    }

    viewModel.folders.observe(
        this,
        {
          ItemEditDialogFragment.newInstance(
              intent.getStringExtra(Intent.EXTRA_SUBJECT) ?: "",
              intent.getStringExtra(Intent.EXTRA_TEXT) ?: "",
              ArrayList(it)
          ).show(supportFragmentManager, ItemEditDialogFragment.TAG)
        }
    )

    viewModel.postResult.observe(
        this,
        {
          Toast.makeText(
              this,
              getString(R.string.snackbar_create_message, it),
              Toast.LENGTH_SHORT
          ).show()
          finish()
        }
    )

    viewModel.getFolders()
  }

  override fun onEdited(itemId: Long, itemName: String, itemUrl: String?, folderId: Long?) {
    analyticsUtils.logResult("share add", "add")
    viewModel.insertItem(itemName, itemUrl!!, folderId!!)
  }

  override fun onCancel() {
    analyticsUtils.logResult("share add", "cancel")
    finish()
  }
}