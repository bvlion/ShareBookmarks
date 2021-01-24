package net.ambitious.android.sharebookmarks.ui.faq

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.remote.etc.FaqEntity
import net.ambitious.android.sharebookmarks.data.remote.etc.FaqEntity.FaqDetail
import net.ambitious.android.sharebookmarks.databinding.RowFaqBinding
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils

class FaqListAdapter(
  private val analyticsUtils: AnalyticsUtils,
  private val faqClickListener: FaqClickListener
) : Adapter<ViewHolder>() {

  private val _items = arrayListOf<FaqDetail>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    FaqViewHolder(RowFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false))

  override fun onBindViewHolder(holder: ViewHolder, position: Int) = (holder as FaqViewHolder).run {
    setItem(position, _items, faqClickListener, analyticsUtils)
  }

  override fun getItemCount() = _items.size + if (_items.isEmpty()) 0 else 2

  fun setItems(entity: FaqEntity) {
    _items.clear()
    _items.addAll(entity.faq)
    notifyDataSetChanged()
  }

  interface FaqClickListener {
    fun onInquiryClick()
  }

  class FaqViewHolder(private val row: RowFaqBinding) : ViewHolder(row.root) {
    fun setItem(
      position: Int,
      items: List<FaqDetail>,
      faqClickListener: FaqClickListener,
      analyticsUtils: AnalyticsUtils
    ) {
      row.inquiryButton.isVisible = position == items.size + 1
      row.inquiryButton.setOnClickListener {
        analyticsUtils.logOtherTap("faq", "onRowClick inquiryButton")
        faqClickListener.onInquiryClick()
      }
      row.header.isVisible = position == 0
      row.title.isVisible = position > 0 && position < items.size + 1
      row.title.setOnClickListener {
        analyticsUtils.logOtherTap("faq", "onRowClick ${items[position - 1].question}")
        AlertDialog.Builder(row.root.context)
            .setTitle(items[position - 1].question)
            .setView(TextView(row.root.context).apply {
              setPadding(context.resources.getDimensionPixelSize(R.dimen.space_normal))
              text =
                HtmlCompat.fromHtml(items[position - 1].answer, HtmlCompat.FROM_HTML_MODE_COMPACT)
            })
            .setPositiveButton(android.R.string.ok, null)
            .create().show()
      }
      if (row.title.isVisible) {
        row.title.text = items[position - 1].question
      }
    }
  }
}