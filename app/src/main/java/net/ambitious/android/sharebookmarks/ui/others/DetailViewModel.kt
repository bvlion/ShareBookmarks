package net.ambitious.android.sharebookmarks.ui.others

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.ambitious.android.sharebookmarks.data.remote.etc.EtcApi
import net.ambitious.android.sharebookmarks.ui.BaseViewModel
import java.util.Locale

class DetailViewModel(private val etcApi: EtcApi) : BaseViewModel() {

  private val _message = MutableLiveData<String>()
  val message: LiveData<String>
    get() = _message

  fun getDetailMessages(isTerm: Boolean) = launch({
    _message.postValue(
        if (isTerm) {
          etcApi.getTermsOfUse().message
        } else {
          etcApi.getPrivacyPolicy().message
        }
    )
  }, {
    _message.postValue("")
  })
}