package net.ambitious.android.sharebookmarks.ui.faq

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.ambitious.android.sharebookmarks.data.remote.etc.EtcApi
import net.ambitious.android.sharebookmarks.data.remote.etc.FaqEntity
import net.ambitious.android.sharebookmarks.ui.BaseViewModel
import java.util.Locale

class FaqViewModel(private val etcApi: EtcApi) : BaseViewModel() {

  private val _faq = MutableLiveData<FaqEntity>()
  val faq: LiveData<FaqEntity>
    get() = _faq

  fun getFaq() {
    launch({
      _faq.postValue(
          etcApi.getFaq(
              if (Locale.getDefault() == Locale.JAPAN) {
                "ja"
              } else {
                "en"
              }
          )
      )
    }, {
      _faq.postValue(FaqEntity(listOf()))
    })
  }
}