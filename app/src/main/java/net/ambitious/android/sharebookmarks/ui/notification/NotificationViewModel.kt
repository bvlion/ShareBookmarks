package net.ambitious.android.sharebookmarks.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.ambitious.android.sharebookmarks.data.remote.notifications.NotificationsApi
import net.ambitious.android.sharebookmarks.data.remote.notifications.NotificationsEntity
import net.ambitious.android.sharebookmarks.ui.BaseViewModel

class NotificationViewModel(private val notificationApi: NotificationsApi) : BaseViewModel() {

  private val _notifications = MutableLiveData<NotificationsEntity>()
  val notifications: LiveData<NotificationsEntity>
    get() = _notifications

  fun getNotifications(isAuth: Boolean) {
    launch {
      if (isAuth) {
        _notifications.postValue(notificationApi.getAuthNotifications())
      } else {
        _notifications.postValue(notificationApi.getNotifications())
      }
    }
  }
}