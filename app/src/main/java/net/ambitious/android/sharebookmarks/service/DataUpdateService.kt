package net.ambitious.android.sharebookmarks.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.widget.Toast
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.source.ShareBookmarksDataSource
import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.NotificationUtils
import org.koin.android.ext.android.inject
import java.lang.Exception

class DataUpdateService : Service() {

  private val dataSource: ShareBookmarksDataSource by inject()

  override fun onBind(intent: Intent?) = Binder()

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY.apply {
    startForeground(
        Const.NotificationService.DATA_UPDATE_ID,
        NotificationUtils.getHomeSituationNotification(this@DataUpdateService)
    )

    GlobalScope.launch {
      withContext(Dispatchers.IO) {

        try {
          dataSource.dataUpdate()
        } catch (e: Exception) {
          FirebaseCrashlytics.getInstance().recordException(e)
          Toast.makeText(this@DataUpdateService, R.string.sync_network_error, Toast.LENGTH_LONG).show()
        }

        stopSelf()
      }
    }
  }
}