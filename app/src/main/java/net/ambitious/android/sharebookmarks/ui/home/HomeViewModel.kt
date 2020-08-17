package net.ambitious.android.sharebookmarks.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.data.local.item.ItemDao
import net.ambitious.android.sharebookmarks.ui.BaseViewModel
import net.ambitious.android.sharebookmarks.util.Const.OwnerType

class HomeViewModel(private val itemDao: ItemDao) : BaseViewModel() {
  private val _items = MutableLiveData<List<Item>>()
  val items: LiveData<List<Item>>
    get() = _items

  private val _parentId = MutableLiveData<Long>()
  private val _ownerId = MutableLiveData<Int>()

  fun setParentId(parentId: Long) {
    _parentId.value = parentId
    getItems()
  }

  fun getItems() {
    launch {
      _items.postValue(itemDao.getItems(_parentId.value ?: 0))
    }
  }

  fun updateItem(
    itemId: Long,
    itemName: String,
    itemUrl: String?
  ) =
    launch {
      if (itemId > 0) {
        val item = itemDao.getItem(itemId)
        itemDao.update(
            Item(
                itemId,
                item.remoteId,
                _parentId.value ?: 0,
                itemName,
                itemUrl,
                item.order,
                item.ownerType
            )
        )
      } else {
        itemDao.insert(
            Item(
                null,
                null,
                _parentId.value ?: 0,
                itemName,
                itemUrl,
                itemDao.getMaxOrder(_parentId.value ?: 0) ?: 0,
                _ownerId.value ?: OwnerType.OWNER.value
            )
        )
      }
      _items.postValue(itemDao.getItems(_parentId.value ?: 0))
    }
}