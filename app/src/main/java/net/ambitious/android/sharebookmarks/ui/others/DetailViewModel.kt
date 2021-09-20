package net.ambitious.android.sharebookmarks.ui.others

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import net.ambitious.android.sharebookmarks.data.remote.etc.EtcApi
import net.ambitious.android.sharebookmarks.ui.BaseViewModel

class DetailViewModel(private val etcApi: EtcApi) : BaseViewModel() {

  private val _isTerm = MutableLiveData<Boolean>()

  val message: LiveData<String> = _isTerm.switchMap {
    liveData {
      runCatching {
        if (it) {
          etcApi.getTermsOfUse().message
        } else {
          etcApi.getPrivacyPolicy().message
        }
      }
        .onSuccess { emit(it) }
        .onFailure { emit("") }
    }
  }

  fun getDetailMessages(isTerm: Boolean) {
    _isTerm.value = isTerm
  }
}