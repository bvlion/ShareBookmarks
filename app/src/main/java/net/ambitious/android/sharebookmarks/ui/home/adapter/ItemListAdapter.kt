package net.ambitious.android.sharebookmarks.ui.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.R.layout
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.Const.ItemType
import net.ambitious.android.sharebookmarks.util.Const.ItemType.FOLDER
import net.ambitious.android.sharebookmarks.util.Const.ItemType.ITEM

class ItemListAdapter(private val context: Context, private val listener: OnItemClickListener) :
    Adapter<ViewHolder>() {
  private val _items = arrayListOf<Item>()
  private var sortMode = false

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
      LayoutInflater.from(parent.context)
          .inflate(layout.row_item, parent, false)
  )

  override fun getItemCount() = _items.size

  @SuppressLint("ClickableViewAccessibility")
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    (holder as ItemViewHolder).apply {
      _items[position].let { item ->
        titleTextView.text = item.name
        if (item.url == null) {
          when (item.ownerType) {
            Const.OwnerType.OWNER.value -> titleImageView.setImageResource(
                R.drawable.ic_item_folder
            )
            Const.OwnerType.EDITABLE.value or Const.OwnerType.READONLY.value -> titleImageView.setImageResource(
                R.drawable.ic_item_folder_shared
            )
            else -> throw RuntimeException("Cannot parse owner type of ${item.ownerType}")
          }
        } else {
          Glide.with(context)
              .load(createThumbnailUrl(item.url))
              .placeholder(R.drawable.ic_item_internet)
              .into(titleImageView)
        }

        menuImage.setOnClickListener { v ->
          PopupMenu(context, v).apply {
            menuInflater.inflate(
                if (item.url == null) {
                  R.menu.row_folder_popup
                } else {
                  R.menu.row_item_popup
                }, menu
            )
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
                    titleImageView,
                    createThumbnailUrl(item.url)
                )
              }
              return@setOnMenuItemClickListener true
            }
          }.show()
        }

        if (sortMode) {
          rowItem.setOnTouchListener { v, event ->
            when (event.actionMasked) {
              MotionEvent.ACTION_DOWN -> listener.onStartDrag(holder)
              MotionEvent.ACTION_UP -> v.performClick()
            }
            v.onTouchEvent(event)
          }
        } else {
          rowItem.setOnTouchListener(null)
          rowItem.setOnLongClickListener {
            listener.onSetSortMode()
            true
          }
          if (item.url.isNullOrEmpty()) {
            rowItem.setOnClickListener { listener.onRowClick(item.id, null) }
          } else {
            rowItem.setOnClickListener { listener.onRowClick(null, item.url) }
          }
        }

        if (sortMode) {
          menuImage.visibility = View.GONE
          sortImage.visibility = View.VISIBLE
        } else {
          menuImage.visibility = View.VISIBLE
          sortImage.visibility = View.GONE
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
    fun onThumbnailUpdateClick(imageView: ImageView, url: String?)
    fun onStartDrag(holder: ViewHolder)
    fun onSetSortMode()
  }

  fun setItems(items: List<Item>) {
    _items.clear()
    _items.addAll(items)
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

  private fun createThumbnailUrl(url: String?) =
    url?.split("://")?.let {
      if (it.size < 2) {
        null
      } else {
        Const.GOOGLE_FAVICON_URL + it[1].split("/")[0]
      }
    }

  class ItemViewHolder internal constructor(itemView: View) :
      ViewHolder(itemView) {
    val titleImageView = itemView.findViewById(R.id.title_image) as ImageView
    val titleTextView = itemView.findViewById(R.id.title) as TextView
    val menuImage = itemView.findViewById(R.id.menu_image) as ImageView
    val sortImage = itemView.findViewById(R.id.menu_sort) as ImageView
    val rowItem = itemView.findViewById(R.id.row_item) as LinearLayout
  }
}