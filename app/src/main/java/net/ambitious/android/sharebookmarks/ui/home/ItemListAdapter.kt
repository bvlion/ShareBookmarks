package net.ambitious.android.sharebookmarks.ui.home

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.util.Const

class ItemListAdapter(private val context: Context, private val listener: OnItemClickListener) :
    Adapter<ViewHolder>() {
  private val _items = arrayListOf<Item>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
      LayoutInflater.from(parent.context)
          .inflate(R.layout.item_row, parent, false)
  )

  override fun getItemCount() = _items.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    (holder as ItemViewHolder).apply {
      _items[position].let { item ->
        titleTextView.text = item.name
        if (item.url == null) {
          when (item.ownerType) {
            Const.OwnerType.OWNER.value -> setCompoundDrawablesWithIntrinsicBounds(
                titleTextView,
                R.drawable.ic_item_folder
            )
            Const.OwnerType.EDITABLE.value or Const.OwnerType.READONLY.value ->
              setCompoundDrawablesWithIntrinsicBounds(
                  titleTextView,
                  R.drawable.ic_item_folder_shared
              )
            else -> throw RuntimeException("Cannot parse owner type of ${item.ownerType}")
          }
        } else {
          // TODO サムネイルを読み込む処理
          setCompoundDrawablesWithIntrinsicBounds(
              titleTextView,
              R.drawable.ic_item_internet
          )
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
                R.id.row_delete -> listener.onDeleteClick(item.id!!)
                R.id.row_edit -> listener.onEditClick(item)
                R.id.row_move -> listener.onMoveClick(item.id!!)
                R.id.row_share -> listener.onShareClick(item.id!!)
                R.id.row_create_shortcut -> listener.onCreateShortcut(item.id!!)
                R.id.row_update_thumbnail -> listener.onThumbnailUpdateClick(item.id!!)
              }
              return@setOnMenuItemClickListener true
            }
          }.show()
        }

        if (TextUtils.isEmpty(item.url)) {
          rowItem.setOnClickListener { listener.onRowClick(item.id, null) }
        } else {
          rowItem.setOnClickListener { listener.onRowClick(null, item.url) }
        }
      }
    }
  }

  interface OnItemClickListener {
    fun onRowClick(item: Long?, url: String?)
    fun onDeleteClick(itemId: Long)
    fun onEditClick(item: Item)
    fun onMoveClick(itemId: Long)
    fun onShareClick(itemId: Long)
    fun onCreateShortcut(itemId: Long)
    fun onThumbnailUpdateClick(itemId: Long)
  }

  fun setItems(items: List<Item>) {
    _items.clear()
    _items.addAll(items)
  }

  private fun setCompoundDrawablesWithIntrinsicBounds(view: TextView, resourceId: Int) =
    view.setCompoundDrawablesWithIntrinsicBounds(resourceId, 0, 0, 0)

  class ItemViewHolder internal constructor(itemView: View) :
      ViewHolder(itemView) {
    val titleTextView = itemView.findViewById(R.id.title) as TextView
    val menuImage = itemView.findViewById(R.id.menu_image) as ImageView
    val rowItem = itemView.findViewById(R.id.row_item) as LinearLayout
  }
}