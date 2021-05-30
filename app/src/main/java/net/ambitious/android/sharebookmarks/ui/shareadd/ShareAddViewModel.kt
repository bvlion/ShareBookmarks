package net.ambitious.android.sharebookmarks.ui.shareadd

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.runBlocking
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.data.local.item.ItemDao
import net.ambitious.android.sharebookmarks.ui.BaseViewModel
import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.Const.OwnerType.OWNER

class ShareAddViewModel(
  private val itemDao: ItemDao
) : BaseViewModel() {

  val folders = runBlocking {
    arrayListOf(Const.HOME_FOLDER).apply { addAll(itemDao.getFolderItems(0)) }
  }

  private val _postResult: MutableLiveData<Triple<Long, String, String>> = MutableLiveData()
  val postResult: LiveData<Triple<Long, String, String>>
    get() = _postResult

  fun insertItem(itemName: String, itemUrl: String, folderId: Long) {
    launch {
      val id = itemDao.insert(
        Item(
          null,
          null,
          folderId,
          itemName,
          itemUrl,
          null,
          0,
          OWNER.value
        )
      )
      _postResult.postValue(
        Triple(
          id,
          itemName,
          itemUrl
        )
      )
    }
  }
}