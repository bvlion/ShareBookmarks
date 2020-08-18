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

  private val _breadcrumbs = MutableLiveData<MutableList<Pair<Long, String>>>()
  val breadcrumbs: LiveData<MutableList<Pair<Long, String>>>
    get() = _breadcrumbs

  private val _breadcrumbsList = arrayListOf<Pair<Long, String>>()

  fun setParentId(parentId: Long) {
    _parentId.value = parentId
    getItems()
  }

  fun getItems() {
    _breadcrumbsList.clear()
    launch {
      createBreadcrumbs(_parentId.value ?: 0L)
      _items.postValue(itemDao.getItems(_parentId.value ?: 0))
    }
  }

  private suspend fun createBreadcrumbs(selfId: Long) {
    if (selfId > 0) {
      itemDao.getItem(selfId)?.let {
        _breadcrumbsList.add(0, Pair(it.id!!, it.name))
        createBreadcrumbs(it.parentId)
      }
    } else {
      _breadcrumbsList.add(0, Pair(0, "Home"))
      _breadcrumbs.postValue(_breadcrumbsList)
    }
  }

  fun updateItem(
    itemId: Long,
    itemName: String,
    itemUrl: String?
  ) =
    launch {
      if (itemId > 0) {
        val item = itemDao.getItem(itemId)!!
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

  fun deleteItem(itemId: Long) = launch {
    deleteItems(itemDao.getItems(itemId))
    itemDao.delete(itemId)
    _items.postValue(itemDao.getItems(_parentId.value ?: 0))
  }

  private fun deleteItems(childItems: List<Item>) {
    childItems.filter { it.url.isNullOrEmpty() }.forEach { deleteItem(it.id!!) }
  }
}