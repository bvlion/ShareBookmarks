package net.ambitious.android.sharebookmarks.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.data.local.item.ItemDao
import net.ambitious.android.sharebookmarks.data.remote.etc.EtcApi
import net.ambitious.android.sharebookmarks.data.remote.users.UsersApi
import net.ambitious.android.sharebookmarks.data.remote.users.UsersEntity.AuthTokenResponse
import net.ambitious.android.sharebookmarks.data.remote.users.UsersEntity.UsersPostData
import net.ambitious.android.sharebookmarks.ui.BaseViewModel
import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.Const.OwnerType
import net.ambitious.android.sharebookmarks.util.OperationUtils
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import org.joda.time.DateTime

class HomeViewModel(
  private val itemDao: ItemDao,
  private val usersApi: UsersApi,
  private val etcApi: EtcApi
) : BaseViewModel() {

  private val _items = MutableLiveData<List<Item>>()
  val items: LiveData<List<Item>>
    get() = _items

  private val _parentId = MutableLiveData<Long>()

  private val _ownerType = MutableLiveData<Int>()
  val ownerType: LiveData<Int>
    get() = _ownerType

  private val _breadcrumbs = MutableLiveData<MutableList<Pair<Long, String>>>()
  val breadcrumbs: LiveData<MutableList<Pair<Long, String>>>
    get() = _breadcrumbs

  private val _folders = MutableLiveData<Pair<Long, List<Item>>>()
  val folders: LiveData<Pair<Long, List<Item>>>
    get() = _folders

  private val _sorting = MutableLiveData<Boolean>()
  val sorting: LiveData<Boolean>
    get() = _sorting

  private val _tokenSave = MutableLiveData<AuthTokenResponse>()
  val tokenSave: LiveData<AuthTokenResponse>
    get() = _tokenSave

  private val _networkError = MutableLiveData<Int>()
  val networkError: LiveData<Int>
    get() = _networkError

  private val _bitmap = MutableLiveData<Bitmap>()
  val bitmap: LiveData<Bitmap>
    get() = _bitmap

  private val _ogpImage = MutableLiveData<Pair<ImageView, String?>>()
  val ogpImage: LiveData<Pair<ImageView, String?>>
    get() = _ogpImage

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

  fun sortSave(items: List<Item>) = launch {
    items.forEachIndexed { index, item -> itemDao.orderUpdate(item.id!!, index + 1) }
    postItems()
  }

  fun getFolders(selfId: Long) = launch {
    val folders = itemDao.getFolderItems(selfId)
    val idList = arrayListOf<Long>()
    // 自身の配下のフォルダを再帰的に除外
    folders.filter { selfId == it.parentId }.map { it.id!! }.forEach { idList.add(it) }
    if (idList.isNotEmpty()) {
      deleteChildItems(folders, idList, idList)
    }
    _folders.postValue(Pair(selfId, folders.filter { !idList.contains(it.id) }))
  }

  fun setFolderNull() = _folders.postValue(null)

  fun sendUserData(email: String, uid: String, token: String, isInitialize: Boolean = false) =
    launch({
      usersApi.userAuth(UsersPostData(email, uid, token)).let {
        _tokenSave.postValue(it)
      }
      if (isInitialize) {
        _networkError.postValue(0)
      } else {
        _networkError.postValue(-1)
      }
    }, {
      if (isInitialize) {
        _networkError.postValue(R.string.sync_network_error)
      }
    })

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
    _breadcrumbsList.clear()
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

  fun moveItem(selfId: Long, parentId: Long) = launch {
    itemDao.move(selfId, parentId, itemDao.getMaxOrder(parentId) ?: 0)
    postItems()
  }

  fun updateItem(
    itemId: Long,
    itemName: String,
    itemUrl: String?
  ) = launch {
    if (itemId > 0) {
      val item = itemDao.getItem(itemId)!!
      itemDao.update(
          Item(
              itemId,
              item.remoteId,
              _parentId.value ?: 0,
              itemName,
              itemUrl,
              item.ogpUrl,
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
              itemUrl?.let { OperationUtils.getOgpImage(it, etcApi) },
              0,
              _ownerType.value ?: OwnerType.OWNER.value
          )
      )
      itemDao.getItems(_parentId.value ?: 0).forEachIndexed { index, it ->
        itemDao.update(
            Item(
                it.id,
                it.remoteId,
                it.parentId,
                it.name,
                it.url,
                it.ogpUrl,
                index + 1,
                it.ownerType,
                it.active,
                DateTime()
            )
        )
      }
    }
    postItems()
  }

  fun deleteItem(itemId: Long) = launch {
    deleteItems(itemDao.getItems(itemId))
    itemDao.delete(itemId)
    postItems()
  }

  @Suppress("BlockingMethodInNonBlockingContext")
  fun getBitmapFromUrl(context: Context, url: String) = launch {
    _bitmap.postValue(
        Glide.with(context).asBitmap()
            .load(OperationUtils.createThumbnailUrl(url)).submit(100, 100).get()
    )
  }

  fun initializeInsert() = launch {
    itemDao.insertAll(
        *Const.INITIALIZE_DB.mapIndexed { index, it ->
          Item(
              null,
              0,
              0,
              it[0]!!,
              it[1],
              it[1]?.let { url -> OperationUtils.getOgpImage(url, etcApi) },
              index + 1,
              OwnerType.OWNER.value
          )
        }.toTypedArray()
    )
    postItems()
  }

  fun updateImage(ogpImageView: ImageView, id: Long, url: String?) = launch {
    val ogp = url?.let {
      OperationUtils.getOgpImage(it, etcApi)
    }
    itemDao.updateOgpImages(ogp, id)
    _ogpImage.postValue(Pair(ogpImageView, ogp))
  }

  private fun deleteItems(childItems: List<Item>) {
    childItems.filter { it.url.isNullOrEmpty() }.forEach { deleteItem(it.id!!) }
  }

  private suspend fun postItems() {
    _ownerType.postValue(itemDao.getItem(_parentId.value ?: 0)?.ownerType ?: 0)
    _items.postValue(itemDao.getItems(_parentId.value ?: 0))
  }
}