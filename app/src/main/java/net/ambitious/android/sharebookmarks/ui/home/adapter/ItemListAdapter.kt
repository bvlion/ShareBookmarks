package net.ambitious.android.sharebookmarks.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.databinding.RowItemBinding
import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.Const.ItemType
import net.ambitious.android.sharebookmarks.util.Const.ItemType.FOLDER
import net.ambitious.android.sharebookmarks.util.Const.ItemType.ITEM
import net.ambitious.android.sharebookmarks.util.OperationUtils

class ItemListAdapter(private val listener: OnItemClickListener) : Adapter<ViewHolder>() {
  private val _items = arrayListOf<Item>()
  private var sortMode = false
  private var _isParentSelf = false

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
    RowItemBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    )
  )

  override fun getItemCount() = _items.size

  @SuppressLint("ClickableViewAccessibility")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    (holder as ItemViewHolder).apply {
      val context = binding.root.context
      _items[position].let { item ->
        binding.title.text = item.name
        binding.ogpImage.isVisible = false
        if (item.url == null) {
          when (item.ownerType) {
            Const.OwnerType.OWNER.value -> binding.titleImage.setImageResource(
              R.drawable.ic_item_folder
            )
            Const.OwnerType.EDITABLE.value, Const.OwnerType.READONLY.value -> binding.titleImage.setImageResource(
              R.drawable.ic_item_folder_shared
            )
            else -> throw RuntimeException("Cannot parse owner type of ${item.ownerType}")
          }
        } else {
          Glide.with(context)
            .load(OperationUtils.createThumbnailUrl(item.url))
            .placeholder(R.drawable.ic_item_internet)
            .into(binding.titleImage)

          if (!item.ogpUrl.isNullOrEmpty()) {
            Glide.with(context)
              .load(item.ogpUrl)
              .centerCrop()
              .into(binding.ogpImage)
            binding.ogpImage.isVisible = true
          }
        }

        binding.menuImage.setOnClickListener { v ->
          PopupMenu(context, v).apply {
            menuInflater.inflate(
              if (item.url == null) {
                if (item.ownerType == Const.OwnerType.OWNER.value) {
                  R.menu.row_folder_popup
                } else {
                  R.menu.row_folder_shared_popup
                }
              } else {
                R.menu.row_item_popup
              }, menu
            )
            menu.findItem(R.id.row_move).isVisible = _isParentSelf
            setOnMenuItemClickListener { menu ->
              when (menu.itemId) {
                R.id.row_delete -> listener.onDeleteClick(
                  item.id!!,
                  item.name,
                  if (item.url.isNullOrEmpty()) {
                    FOLDER
                  } else {
                    ITEM
                  }
                )
                R.id.row_edit -> listener.onEditClick(item)
                R.id.row_move -> listener.onMoveClick(item.id!!)
                R.id.row_share -> listener.onShareClick(item.id!!, item.url)
                R.id.row_create_shortcut -> listener.onCreateShortcut(
                  item.id!!,
                  item.url!!,
                  item.name
                )
                R.id.row_update_thumbnail -> listener.onThumbnailUpdateClick(
                  binding.ogpImage,
                  item.url,
                  item.id!!
                )
              }
              return@setOnMenuItemClickListener true
            }
          }.show()
        }

        if (sortMode) {
          binding.rowItem.setOnTouchListener { v, event ->
            when (event.actionMasked) {
              MotionEvent.ACTION_DOWN -> listener.onStartDrag(holder)
              MotionEvent.ACTION_UP -> v.performClick()
            }
            v.onTouchEvent(event)
          }
        } else {
          binding.rowItem.setOnTouchListener(null)
          binding.rowItem.setOnLongClickListener {
            listener.onSetSortMode()
            true
          }
          if (item.url.isNullOrEmpty()) {
            binding.rowItem.setOnClickListener { listener.onRowClick(item.id, null) }
          } else {
            binding.rowItem.setOnClickListener { listener.onRowClick(null, item.url) }
          }
        }

        if (sortMode) {
          binding.menuImage.visibility = View.GONE
          binding.sortImage.visibility = View.VISIBLE
        } else {
          binding.menuImage.visibility = View.VISIBLE
          binding.sortImage.visibility = View.GONE
        }
      }
    }
  }

  interface OnItemClickListener {
    fun onRowClick(item: Long?, url: String?)
    fun onDeleteClick(itemId: Long, itemName: String, itemType: ItemType)
    fun onEditClick(item: Item)
    fun onMoveClick(itemId: Long)
    fun onShareClick(itemId: Long, url: String?)
    fun onCreateShortcut(itemId: Long, url: String, name: String)
    fun onThumbnailUpdateClick(ogpView: ImageView, url: String?, id: Long)
    fun onStartDrag(holder: ViewHolder)
    fun onSetSortMode()
  }

  fun setItems(items: List<Item>, isParentSelf: Boolean) {
    _items.clear()
    _items.addAll(items)
    _isParentSelf = isParentSelf
    notifyDataSetChanged()
  }

  fun setSortable(isSortMode: Boolean) {
    sortMode = isSortMode
  }

  fun getItems() = _items

  fun moveItem(fromPosition: Int, toPosition: Int) {
    notifyItemMoved(fromPosition, toPosition)
    val item: Item = _items[fromPosition]
    _items.removeAt(fromPosition)
    _items.add(toPosition, item)
  }

  class ItemViewHolder(val binding: RowItemBinding) : ViewHolder(binding.root)
}