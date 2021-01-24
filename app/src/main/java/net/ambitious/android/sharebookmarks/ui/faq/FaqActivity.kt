package net.ambitious.android.sharebookmarks.ui.faq

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.databinding.ActivityApiListViewBinding
import net.ambitious.android.sharebookmarks.ui.BaseActivity
import net.ambitious.android.sharebookmarks.ui.inquiry.InquiryActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class FaqActivity : BaseActivity(), FaqListAdapter.FaqClickListener {

  private val viewModel by viewModel<FaqViewModel>()
  private lateinit var binding: ActivityApiListViewBinding
  private lateinit var adapter: FaqListAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    analyticsUtils.logStartActivity("FaqActivity")

    binding = ActivityApiListViewBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setTitle(R.string.questions_title)
  }

  override fun onStart() {
    super.onStart()

    viewModel.faq.observe(this, {
      binding.loading.visibility = View.GONE
      binding.refresh.isRefreshing = false
      binding.errorText.isVisible = it.faq.isEmpty()
      adapter.setItems(it)
    })

    adapter = FaqListAdapter(analyticsUtils, this)
    binding.recyclerView.layoutManager = LinearLayoutManager(this)
    binding.recyclerView.adapter = adapter

    binding.refresh.setOnRefreshListener {
      viewModel.getFaq()
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.getFaq()
  }

  override fun onInquiryClick() {
    startActivity(Intent(this, InquiryActivity::class.java))
  }
}