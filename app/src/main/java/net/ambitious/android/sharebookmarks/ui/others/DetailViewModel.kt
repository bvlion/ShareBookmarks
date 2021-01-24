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

  fun getDetailMessages(isTerm: Boolean) {
    val lang = if (Locale.getDefault() == Locale.JAPAN) {
      "ja"
    } else {
      "en"
    }

    launch({
      _message.postValue(
          if (isTerm) {
            etcApi.getTermsOfUse(lang).message
          } else {
            etcApi.getPrivacyPolicy(lang).message
          }
      )
    }, {
      _message.postValue("")
    })
  }
}