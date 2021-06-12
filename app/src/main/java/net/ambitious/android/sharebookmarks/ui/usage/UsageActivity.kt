package net.ambitious.android.sharebookmarks.ui.usage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.databinding.ActivityUsageBinding
import net.ambitious.android.sharebookmarks.ui.BaseActivity

class UsageActivity : BaseActivity() {

  private lateinit var binding: ActivityUsageBinding
  private var fromDialog = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityUsageBinding.inflate(layoutInflater)

    setContentView(binding.root)
    setTitle(R.string.menu_how_to_use)

    fromDialog = savedInstanceState?.getBoolean(PARAM_FROM_DIALOG)
      ?: (intent.extras?.getBoolean(PARAM_FROM_DIALOG) ?: false)

    if (fromDialog) {
      supportActionBar?.run {
        setDisplayHomeAsUpEnabled(true)
        setHomeAsUpIndicator(R.drawable.ic_clear_white)
      }
    }
  }

  override fun onStart() {
    super.onStart()
    binding.usageRecyclerView.layoutManager = LinearLayoutManager(this)
    binding.usageRecyclerView.adapter = UsageAdapter()
  }

  override fun finish() {
    super.finish()
    if (fromDialog) {
      overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    } else {
      overridePendingTransition(R.anim.slide_out_right, android.R.anim.fade_out)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putBoolean(PARAM_FROM_DIALOG, fromDialog)
  }

  companion object {
    private const val PARAM_FROM_DIALOG = "from_dialog"

    fun createIntent(context: Context) = Intent(context, UsageActivity::class.java)

    fun createFromDialogIntent(context: Context) = createIntent(context).apply {
      putExtra(PARAM_FROM_DIALOG, true)
    }
  }
}