package net.ambitious.android.sharebookmarks.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import net.ambitious.android.sharebookmarks.data.remote.notifications.NotificationsApi
import net.ambitious.android.sharebookmarks.data.remote.notifications.NotificationsEntity
import net.ambitious.android.sharebookmarks.ui.BaseViewModel
import net.ambitious.android.sharebookmarks.util.PreferencesUtils

class NotificationViewModel(
  private val notificationApi: NotificationsApi,
  private val preferences: PreferencesUtils.Data
) : BaseViewModel() {

  private val _refresh = MutableLiveData<Boolean>()

  init {
    refresh()
  }

  val notifications: LiveData<NotificationsEntity> = _refresh.switchMap {
    liveData {
      runCatching {
        if (preferences.userBearer != null) {
          notificationApi.getAuthNotifications()
        } else {
          notificationApi.getNotifications()
        }
      }
        .onSuccess { emit(it) }
        .onFailure { emit(NotificationsEntity(listOf())) }
    }
  }

  fun refresh() {
    _refresh.value = !(_refresh.value ?: true)
  }
}