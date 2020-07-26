package net.ambitious.android.sharebookmarks.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.data.local.item.ItemDao

class HomeViewModel(private val itemDao: ItemDao) : ViewModel() {
  private val _items = MutableLiveData<List<Item>>()
  val items: LiveData<List<Item>>
    get() = _items

  fun getItems(parentId: Long) {
//    viewModelScope.launch {
//      _items.postValue(itemDao.getItems(parentId))
//    }

    // TODO
    val item = arrayListOf<Item>()
    item.add(
        Item(
            1,
            null,
            0,
            "テストフォルダ",
            null,
            1,
            0
        )
    )
    item.add(
        Item(
            2,
            null,
            0,
            "テストリンク",
            "http://example.com",
            2,
            0
        )
    )
    _items.postValue(item)
  }
}