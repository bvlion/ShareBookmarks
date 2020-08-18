package net.ambitious.android.sharebookmarks.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.ambitious.android.sharebookmarks.R

class BreadcrumbsAdapter(private val listener: OnBreadcrumbsClickListener) : Adapter<ViewHolder>() {
  private val _breadcrumbs = arrayListOf<Pair<Long, String>>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BreadcrumbsViewHolder(
      LayoutInflater.from(parent.context)
          .inflate(R.layout.row_breadcrumbs, parent, false)
  )

  override fun getItemCount() = _breadcrumbs.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    (holder as BreadcrumbsViewHolder).apply {
      _breadcrumbs[position].let { row ->
        nameTextView.apply {
          text = row.second
          setOnClickListener { listener.onRowClick(row.first) }
        }
      }
      if (position == _breadcrumbs.size - 1) {
        nextTextView.visibility = View.GONE
      } else {
        nextTextView.visibility = View.VISIBLE
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

  class BreadcrumbsViewHolder internal constructor(itemView: View) :
      ViewHolder(itemView) {
    val nameTextView = itemView.findViewById(R.id.folder_name) as TextView
    val nextTextView = itemView.findViewById(R.id.next_icon) as TextView
  }
}