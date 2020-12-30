package net.ambitious.android.sharebookmarks.ui.usage

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.databinding.ActivityUsageBinding
import net.ambitious.android.sharebookmarks.ui.BaseActivity

class UsageActivity : BaseActivity() {

  private lateinit var binding: ActivityUsageBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityUsageBinding.inflate(layoutInflater)

    setContentView(binding.root)
    setTitle(R.string.menu_how_to_use)
  }

  override fun onStart() {
    super.onStart()
    binding.usageRecyclerView.layoutManager = LinearLayoutManager(this)
    binding.usageRecyclerView.adapter = UsageAdapter()
  }
}