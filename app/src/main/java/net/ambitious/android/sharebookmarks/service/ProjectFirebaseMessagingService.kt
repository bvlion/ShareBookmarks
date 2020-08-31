package net.ambitious.android.sharebookmarks.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import org.koin.android.ext.android.inject

class ProjectFirebaseMessagingService : FirebaseMessagingService() {

  private val preferences: PreferencesUtils.Data by inject()

  override fun onNewToken(token: String) {
    preferences.fcmToken = token
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
  }
}