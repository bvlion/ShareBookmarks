package net.ambitious.android.sharebookmarks.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.data.local.item.ItemDao
import net.ambitious.android.sharebookmarks.data.remote.users.UsersApi
import net.ambitious.android.sharebookmarks.data.remote.users.UsersEntity.UsersPostData
import net.ambitious.android.sharebookmarks.ui.BaseViewModel
import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.Const.OwnerType
import net.ambitious.android.sharebookmarks.util.PreferencesUtils

class HomeViewModel(
  private val itemDao: ItemDao,
  private val usersApi: UsersApi
) : BaseViewModel() {
  private val _items = MutableLiveData<List<Item>>()
  val items: LiveData<List<Item>>
    get() = _items

  private val _parentId = MutableLiveData<Long>()
  private val _ownerId = MutableLiveData<Int>()

  private val _breadcrumbs = MutableLiveData<MutableList<Pair<Long, String>>>()
  val breadcrumbs: LiveData<MutableList<Pair<Long, String>>>
    get() = _breadcrumbs

  private val _folders = MutableLiveData<Pair<Long, List<Item>>>()
  val folders: LiveData<Pair<Long, List<Item>>>
    get() = _folders

  private val _sorting = MutableLiveData<Boolean>()
  val sorting: LiveData<Boolean>
    get() = _sorting

  private val _tokenSave = MutableLiveData<String>()
  val tokenSave: LiveData<String>
    get() = _tokenSave

  private val _breadcrumbsList = arrayListOf<Pair<Long, String>>()

  fun setInitialParentId(parentId: Long?) {
    _parentId.value = parentId
  }

  fun setParentId(parentId: Long, preferences: PreferencesUtils.Data) {
    _parentId.value = parentId
    getItems()
    if (preferences.startFolder == Const.StartFolderType.LAST.value) {
      preferences.startFolderId = parentId
    }
  }

  fun getItems() {
    _breadcrumbsList.clear()
    launch {
      createBreadcrumbs(_parentId.value ?: 0L)
      postItems()
    }
  }

  fun sortModeChange(start: Boolean) {
    if (_sorting.value == start) {
      return
    }
    _sorting.value = start
    launch { postItems() }
  }

  fun sortSave(items: List<Item>) {
    launch {
      items.forEachIndexed { index, item -> itemDao.orderUpdate(item.id!!, index + 1) }
    }
  }

  fun getFolders(selfId: Long) {
    launch {
      val folders = itemDao.getFolderItems(selfId)
      val idList = arrayListOf<Long>()
      // 自身の配下のフォルダを再帰的に除外
      folders.filter { selfId == it.parentId }.map { it.id!! }.forEach { idList.add(it) }
      if (idList.isNotEmpty()) {
        deleteChildItems(folders, idList, idList)
      }
      _folders.postValue(Pair(selfId, folders.filter { !idList.contains(it.id) }))
    }
  }

  fun sendUserData(email: String, token: String) {
    launch {
      usersApi.userAuth(UsersPostData(email, token)).accessToken.let {
        _tokenSave.postValue(it)
      }
    }
  }

  private fun deleteChildItems(
    items: List<Item>,
    idAllList: ArrayList<Long>,
    idCheckList: ArrayList<Long>
  ) {
    val idNextList = arrayListOf<Long>()
    // チェック対象の配下のフォルダの ID を取得
    items.filter { idCheckList.contains(it.parentId) }
        .map { it.id!! }
        .forEach { idNextList.add(it) }
    // チェックが完了に伴い全件リストに格納
    idAllList.addAll(idCheckList)
    // まだ配下にフォルダがあればこの処理を再度呼び出す
    if (idNextList.isNotEmpty()) {
      deleteChildItems(items, idAllList, idNextList)
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

  fun moveItem(selfId: Long, parentId: Long) {
    launch {
      itemDao.move(selfId, parentId, itemDao.getMaxOrder(parentId) ?: 0)
      postItems()
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
      postItems()
    }

  fun deleteItem(itemId: Long) = launch {
    deleteItems(itemDao.getItems(itemId))
    itemDao.delete(itemId)
    postItems()
  }

  private fun deleteItems(childItems: List<Item>) {
    childItems.filter { it.url.isNullOrEmpty() }.forEach { deleteItem(it.id!!) }
  }

  private suspend fun postItems() {
    _items.postValue(itemDao.getItems(_parentId.value ?: 0))
  }
}