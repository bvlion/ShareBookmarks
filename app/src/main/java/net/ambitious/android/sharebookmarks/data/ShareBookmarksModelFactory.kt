package net.ambitious.android.sharebookmarks.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.ambitious.android.sharebookmarks.data.local.ShareBookmarksDatabase
import net.ambitious.android.sharebookmarks.ui.home.HomeViewModel

class ShareBookmarksModelFactory(private val context: Context) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    when (modelClass) {
      HomeViewModel::class.java -> {
        return HomeViewModel(ShareBookmarksDatabase.getInstance(context)) as T
      }
      else -> {
        throw RuntimeException("Cannot create an instance of $modelClass")
      }
    }
  }

  companion object {
    private var instance: ShareBookmarksModelFactory? = null
    private val lock = Any()

    fun getInstance(context: Context) =
      instance ?: synchronized(lock) {
        ShareBookmarksModelFactory(context).also { instance = it }
      }
  }
}