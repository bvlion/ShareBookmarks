package net.ambitious.android.sharebookmarks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

  fun launch(
    block: suspend CoroutineScope.() -> Unit,
    errorCallback: (() -> Unit)? = null
  ) = viewModelScope.launch(Dispatchers.IO) {
    try {
      block()
    } catch (e: Exception) {
      FirebaseCrashlytics.getInstance().recordException(e)
      errorCallback?.invoke()
    }
  }

  fun launch(
    block: suspend CoroutineScope.() -> Unit
  ) = launch(block, null)
}
