package net.ambitious.android.sharebookmarks.ui.inquiry

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.ambitious.android.sharebookmarks.BuildConfig
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.remote.contact.ContactApi
import net.ambitious.android.sharebookmarks.ui.BaseViewModel

class InquiryViewModel(
  private val context: Context,
  private val contactApi: ContactApi
) : BaseViewModel() {

  private val _postResult: MutableLiveData<Boolean> = MutableLiveData()
  val postResult: LiveData<Boolean>
    get() = _postResult

  fun postMessage() {
    launch {
      if (BuildConfig.CONTACT_URL.isEmpty()) {
        true
      } else {
        contactApi.postContact(mailAddress.value!!, inquiry.value!!).isNotEmpty()
      }.let {
        _postResult.postValue(it)
        _isSendButtonEnabled.postValue(!it)
      }
    }
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

  fun mailAddressCheck(text: String) {
    _mailAddress.value = text
    inputValidation()
  }

  fun inquiryCheck(text: String) {
    _inquiry.value = text
    inputValidation()
  }

  private fun inputValidation() {
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