package net.ambitious.android.sharebookmarks.ui.extensions

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.google.android.material.textfield.TextInputLayout

object Binding {
  @BindingAdapter("errorText")
  @JvmStatic
  fun TextInputLayout.errorText(message: LiveData<String>) {
    error = message.value
  }
}