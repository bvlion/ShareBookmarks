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

  private val _saved = MutableLiveData<Boolean>()
  val saved: LiveData<Boolean>
    get() = _saved

  var changed = false
  private var _folderId = 0L

  fun getShares(folderId: Long) {
    if (_folderId.value == null) {
      launch {
        _folderId.postValue(folderId)
        _share.postValue(shareDao.getShares(folderId))
      }
    }
  }

  fun addList(contact: Contact) {
    if (_share.value!!.none { it.userEmail == contact.email }) {
      _share.value = ArrayList(_share.value!!).apply {
        add(
          Share(
            null,
            null,
            _folderId,
            contact.email,
            contact.displayName,
            contact.icon,
            OwnerType.EDITABLE.value
          )
        )
      }
      changed = true
    }
  }

  fun deleteList(position: Int) {
    _share.value = ArrayList(_share.value!!).apply {
      removeAt(position)
    }
    changed = true
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
    _share.postValue(shareDao.getShares(_folderId))
  }

  fun save() = launch {
    val dbData = shareDao.getShares(_folderId)

    // DB にあって List にない値を削除
    shareDao.delete(
      *dbData
        .filter { db -> _share.value!!.none { db.userEmail == it.userEmail } }
        .map { it.id!! }.toLongArray()
    )

    val shareList = ArrayList(_share.value!!)
    // Email ではないものを削除
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
            _folderId,
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