package net.ambitious.android.sharebookmarks.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.ambitious.android.sharebookmarks.databinding.RowBreadcrumbsBinding

class BreadcrumbsAdapter(private val listener: OnBreadcrumbsClickListener) : Adapter<ViewHolder>() {
  private val _breadcrumbs = arrayListOf<Pair<Long, String>>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BreadcrumbsViewHolder(
      RowBreadcrumbsBinding.inflate(
          LayoutInflater.from(parent.context),
          parent,
          false
      )
  )

  override fun getItemCount() = _breadcrumbs.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    (holder as BreadcrumbsViewHolder).apply {
      _breadcrumbs[position].let { row ->
        binding.folderName.apply {
          text = row.second
          setOnClickListener { listener.onRowClick(row.first) }
        }
      }
      binding.nextIcon.visibility =
        if (position == _breadcrumbs.size - 1) {
          View.GONE
        } else {
          View.VISIBLE
        }
    }
  }

  interface OnBreadcrumbsClickListener {
    fun onRowClick(id: Long)
  }

  fun setBreadcrumbs(breadcrumbs: List<Pair<Long, String>>) {
    _breadcrumbs.clear()
    _breadcrumbs.addAll(breadcrumbs)
    notifyDataSetChanged()
  }

  class BreadcrumbsViewHolder(val binding: RowBreadcrumbsBinding) : ViewHolder(binding.root)
}