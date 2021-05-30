package net.ambitious.android.sharebookmarks.ui.share.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.ambitious.android.sharebookmarks.R

class InfoViewHolder(view: View) : ViewHolder(view) {
  val infoMessage = view.findViewById(R.id.info_massage) as AppCompatTextView
}