package net.ambitious.android.sharebookmarks.ui.inquiry

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import net.ambitious.android.sharebookmarks.BuildConfig
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.remote.contact.ContactApi
import net.ambitious.android.sharebookmarks.data.remote.contact.ContactEntity
import net.ambitious.android.sharebookmarks.ui.BaseViewModel
import org.json.JSONObject
import java.lang.Exception

class InquiryViewModel(
  private val contactApi: ContactApi
) : BaseViewModel() {

  private val _postResult: MutableLiveData<Boolean> = MutableLiveData()
  val postResult: LiveData<Boolean>
    get() = _postResult

  fun postMessage() {
    _isSendButtonEnabled.value = false
    launch({
      if (BuildConfig.CONTACT_URL.isEmpty()) { // 開発環境
        "{\"ok\":true}"
      } else {
        contactApi.postContact(ContactEntity(mailAddress.value!!, inquiry.value!!))
      }.let {
        try {
          JSONObject(it.toString())
          _postResult.postValue(true)
        } catch (_: Exception) {
          FirebaseCrashlytics.getInstance().recordException(Exception(it.toString()))
          _postResult.postValue(false)
          _isSendButtonEnabled.postValue(true)
        }
      }
    }, {
      _postResult.postValue(false)
      _isSendButtonEnabled.postValue(true)
    })
  }

  private val _mailAddress: MutableLiveData<String> = MutableLiveData()
  val mailAddress: LiveData<String>
    get() = _mailAddress

  private val _mailAddressError: MutableLiveData<String> = MutableLiveData()
  val mailAddressError: LiveData<String>
    get() = _mailAddressError

  private val _isMailEnabled: MutableLiveData<Boolean> = MutableLiveData()
  val isMailEnabled: LiveData<Boolean>
    get() = _isMailEnabled

  private val _inquiry: MutableLiveData<String> = MutableLiveData()
  val inquiry: LiveData<String>
    get() = _inquiry

  private val _inquiryError: MutableLiveData<String> = MutableLiveData()
  val inquiryError: LiveData<String>
    get() = _inquiryError

  private val _isSendButtonEnabled: MutableLiveData<Boolean> = MutableLiveData()
  val isSendButtonEnabled: LiveData<Boolean>
    get() = _isSendButtonEnabled

  fun initializeView() {
    _isMailEnabled.value = true
    _isSendButtonEnabled.value = false
  }

  fun setInitialMailAddress(mail: String) {
    _mailAddress.value = mail
    _isMailEnabled.value = false
  }

  fun mailAddressCheck(text: String, context: Context) {
    _mailAddress.value = text
    inputValidation(context)
  }

  fun inquiryCheck(text: String, context: Context) {
    _inquiry.value = text
    inputValidation(context)
  }

  private fun inputValidation(context: Context) {
    _inquiryError.value = inquiry.value?.let {
      if (it.isEmpty()) {
        context.getString(R.string.inquiry_validation)
      } else {
        ""
      }
    } ?: ""

    _mailAddressError.value = mailAddress.value?.let {
      if (it.isEmpty()) {
        context.getString(R.string.email_validation)
      } else {
        ""
      }
    } ?: ""

    _isSendButtonEnabled.value =
      !inquiry.value.isNullOrEmpty()
          && !mailAddress.value.isNullOrEmpty()
          && !(_postResult.value ?: false)
  }

  interface OnClickListener {
    fun onSendClick()
  }
}