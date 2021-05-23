package net.ambitious.android.sharebookmarks.ui.usage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.databinding.RowUsageBinding

class UsageAdapter : Adapter<ViewHolder>() {

  private val items =
    arrayListOf<ViewType>().apply {
      add(ViewType.SPACE)
      repeat(IMAGES_COUNT) { add(ViewType.IMAGE) }
      add(ViewType.SPACE)
    }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    when (viewType) {
      ViewType.SPACE.type -> Space(parent)
      else -> ItemViewHolder(
        RowUsageBinding.inflate(
          LayoutInflater.from(parent.context),
          parent,
          false
        )
      )
    }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    if (items[position] == ViewType.IMAGE) {
      (holder as ItemViewHolder).setData(position)
    }
  }

  override fun getItemViewType(position: Int) = items[position].type

  override fun getItemCount() = items.size

  companion object {
    const val IMAGES_COUNT = 7
  }

  enum class ViewType(val type: Int) {
    SPACE(1),
    IMAGE(2)
  }

  class ItemViewHolder(val binding: RowUsageBinding) : ViewHolder(binding.root) {
    fun setData(position: Int) {
      val context = binding.root.context
      val resources = context.resources

      binding.usageTitle.setText(
        resources.getIdentifier(
          "how_to_use_text_main_${position}", "string",
          context.packageName
        )
      )

      binding.usageImage.run {
        if (position == 1) {
          isVisible = false
        } else {
          isVisible = true
          setImageResource(
            resources.getIdentifier(
              "ic_how_to_use_${position - 1}", "drawable",
              context.packageName
            )
          )
        }
      }

      binding.usageText.setText(
        resources.getIdentifier(
          "how_to_use_text_sub_${position}", "string",
          context.packageName
        )
      )
    }
  }

  private class Space(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
      .inflate(R.layout.row_usage_space, parent, false)
  )
}