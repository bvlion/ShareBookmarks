package net.ambitious.android.sharebookmarks.ui.share

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.ambitious.android.sharebookmarks.data.local.contact.Contact
import net.ambitious.android.sharebookmarks.data.local.share.Share
import net.ambitious.android.sharebookmarks.data.local.share.ShareDao
import net.ambitious.android.sharebookmarks.ui.BaseViewModel
import net.ambitious.android.sharebookmarks.util.Const.OwnerType

class ShareUserViewModel(private val shareDao: ShareDao) : BaseViewModel() {

  private val _share = MutableLiveData<List<Share>>()
  val share: LiveData<List<Share>>
    get() = _share

  private val _changed = MutableLiveData<Boolean>()
  val changed: LiveData<Boolean>
    get() = _changed

  private val _saved = MutableLiveData<Boolean>()
  val saved: LiveData<Boolean>
    get() = _saved

  private val _folderId = MutableLiveData<Long>()

  fun getShares(folderId: Long) {
    if (_folderId.value == null) {
      launch {
        _folderId.postValue(folderId)
        _share.postValue(shareDao.getShares(folderId))
      }
    }
  }

  fun addList(contact: Contact) {
    if (_share.value!!.any { it.userEmail == contact.email }) {
      _share.value = _share.value
    } else {
      _share.value = ArrayList(_share.value!!).apply {
        add(
          Share(
            null,
            null,
            _folderId.value ?: 0,
            contact.email,
            contact.displayName,
            contact.icon,
            OwnerType.EDITABLE.value
          )
        )
      }
      _changed.value = true
    }
  }

  fun deleteList(position: Int) {
    _share.value = ArrayList(_share.value!!).apply {
      removeAt(position)
    }
    _changed.value = true
  }

  fun updateUserContact(contacts: List<Contact>) = launch {
    shareDao.getAllShares()
      .filter { it.userName == null }
      .forEach { share ->
        contacts.firstOrNull { it.email == share.userEmail }?.let {
          shareDao.update(
            Share(
              share.id,
              share.remoteId,
              share.folderId,
              share.userEmail,
              it.displayName,
              it.icon,
              share.ownerType
            )
          )
        }
      }
    _share.postValue(shareDao.getShares(_folderId.value!!))
  }

  fun save() = launch {
    val dbData = shareDao.getShares(_folderId.value!!)

    // DB にあって List にない値を削除
    shareDao.delete(
      *dbData
        .filter { db -> _share.value!!.none { db.userEmail == it.userEmail } }
        .map { it.id!! }.toLongArray()
    )

    val shareList = ArrayList(_share.value!!)
    // Email 出ないものを削除
    shareList.filter {
      !Patterns.EMAIL_ADDRESS.matcher(it.userEmail).matches()
    }.forEach { shareList.remove(it) }

    // DB にも List にもある値は更新して List から削除
    _share.value!!
      .filter { list -> dbData.any { list.userEmail == it.userEmail } }
      .forEach {
        val db = dbData.first { db -> db.userEmail == it.userEmail }
        shareDao.update(
          Share(
            db.id,
            db.remoteId,
            _folderId.value!!,
            it.userEmail,
            it.userName,
            it.userIcon,
            it.ownerType
          )
        )
        shareList.remove(it)
      }

    // 登録
    shareDao.insertAll(*shareList.toTypedArray())

    _saved.postValue(true)
  }
}