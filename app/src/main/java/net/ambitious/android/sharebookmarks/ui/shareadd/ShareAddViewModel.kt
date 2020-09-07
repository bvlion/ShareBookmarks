package net.ambitious.android.sharebookmarks.ui.shareadd

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.data.local.item.ItemDao
import net.ambitious.android.sharebookmarks.ui.BaseViewModel
import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.Const.OwnerType

class ShareAddViewModel(private val itemDao: ItemDao) : BaseViewModel() {

  private val _folders = MutableLiveData<List<Item>>()
  val folders: LiveData<List<Item>>
    get() = _folders

  private val _postResult: MutableLiveData<String> = MutableLiveData()
  val postResult: LiveData<String>
    get() = _postResult

  fun getFolders() {
    launch {
      _folders.postValue(arrayListOf(Const.HOME_FOLDER).apply { addAll(itemDao.getFolderItems(0)) })
    }
  }

  fun insertItem(itemName: String, itemUrl: String, folderId: Long) {
    launch {
      itemDao.insert(
          Item(
              null,
              null,
              folderId,
              itemName,
              itemUrl,
              itemDao.getMaxOrder(folderId) ?: 0,
              OwnerType.OWNER.value
          )
      )
      _postResult.postValue(itemName)
    }
  }
}