package net.ambitious.android.sharebookmarks.ui.faq

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import net.ambitious.android.sharebookmarks.data.remote.etc.EtcApi
import net.ambitious.android.sharebookmarks.data.remote.etc.FaqEntity
import net.ambitious.android.sharebookmarks.ui.BaseViewModel
import java.util.Locale

class FaqViewModel(private val etcApi: EtcApi) : BaseViewModel() {

  private val _refresh = MutableLiveData<Boolean>()

  init {
    refresh()
  }

  val faq = _refresh.switchMap {
    liveData {
      runCatching {
        etcApi.getFaq(
          if (Locale.getDefault() == Locale.JAPAN) {
            "ja"
          } else {
            "en"
          }
        )
      }
        .onSuccess { emit(it) }
        .onFailure { emit(FaqEntity(listOf())) }
    }
  }

  fun refresh() {
    _refresh.value = !(_refresh.value ?: true)
  }
}