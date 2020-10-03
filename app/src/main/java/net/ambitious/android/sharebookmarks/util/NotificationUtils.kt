package net.ambitious.android.sharebookmarks.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import net.ambitious.android.sharebookmarks.R

object NotificationUtils {

  fun getHomeSituationNotification(
    context: Context,
    messageId: Int = R.string.notification_update_message
  ): Notification =
    NotificationCompat.Builder(context, Const.NotificationService.DATA_UPDATE_CHANNEL)
        .setSmallIcon(R.drawable.ic_bookmarks)
        .setShowWhen(false)
        .setContentText(context.getString(messageId))
        .setAutoCancel(false)
        .build()

  @RequiresApi(VERSION_CODES.O)
  fun createChannels(context: Context) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val noticeChannel = NotificationChannel(
        Const.NotificationService.NOTICE_CHANNEL,
        context.getString(R.string.notification_channel_from_app),
        NotificationManager.IMPORTANCE_DEFAULT
    )
    noticeChannel.setShowBadge(true)
    notificationManager.createNotificationChannel(noticeChannel)

    noticeChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_SECRET
    notificationManager.createNotificationChannel(noticeChannel)

    val updateChannel = NotificationChannel(
        Const.NotificationService.DATA_UPDATE_CHANNEL,
        context.getString(R.string.notification_channel_work_server),
        NotificationManager.IMPORTANCE_LOW
    )
    updateChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_SECRET
    notificationManager.createNotificationChannel(updateChannel)
  }
}