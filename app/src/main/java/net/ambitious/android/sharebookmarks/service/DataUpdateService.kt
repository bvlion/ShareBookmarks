package net.ambitious.android.sharebookmarks.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.source.ShareBookmarksDataSource
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils
import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.NotificationUtils
import net.ambitious.android.sharebookmarks.util.OperationUtils
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import org.koin.android.ext.android.inject

class DataUpdateService : Service() {

  private val dataSource: ShareBookmarksDataSource by inject()
  private val analyticsUtils: AnalyticsUtils by inject()
  private val preferences: PreferencesUtils.Data by inject()

  override fun onBind(intent: Intent?) = Binder()

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY.apply {
    val start = System.currentTimeMillis()
    startForeground(
        Const.NotificationService.DATA_UPDATE_ID,
        NotificationUtils.getHomeSituationNotification(this@DataUpdateService)
    )

    val param = intent?.getStringExtra(SYNC_KEY) ?: return@apply

    GlobalScope.launch {
      coroutineScope {
        try {
          when (param) {
            SYNC_PARAM_ALL -> {
              dataSource.syncAll()
              preferences.latestSync = OperationUtils.datetimeFormat(null)
              preferences.shareSynced = true
            }
            SYNC_PARAM_SHARE -> {
              dataSource.syncShares()
              preferences.shareSynced = true
            }
            SYNC_PARAM_ANY_TIME -> {
              dataSource.syncItems(preferences.latestSync)
              preferences.latestSync = OperationUtils.datetimeFormat(null)
              if (!preferences.shareSynced) {
                dataSource.syncShares()
                preferences.shareSynced = true
              }
            }
          }
          sendMessage(R.string.sync_success)
        } catch (e: Exception) {
          FirebaseCrashlytics.getInstance().recordException(e)
          sendMessage(R.string.sync_network_error)
        }

        stopSelf()
        analyticsUtils.logDataUpdateTime(System.currentTimeMillis() - start)
      }
    }
  }

  companion object {
    private const val SYNC_KEY = "sync_key"
    private const val SYNC_PARAM_ALL = "sync_param_all"
    private const val SYNC_PARAM_SHARE = "sync_param_share"
    private const val SYNC_PARAM_ANY_TIME = "sync_param_any_time"

    fun startAllSync(context: Context) = startSync(context, SYNC_PARAM_ALL)

    fun startShareSync(context: Context) = startSync(context, SYNC_PARAM_SHARE)

    fun startItemSync(context: Context) = startSync(context, SYNC_PARAM_ANY_TIME)

    private fun startSync(context: Context, param: String) =
      ContextCompat.startForegroundService(
          context,
          Intent(context, DataUpdateService::class.java).apply {
            putExtra(SYNC_KEY, param)
          })
  }

  private fun sendMessage(messageId: Int) =
    sendBroadcast(Intent().apply {
      putExtra(Const.MESSAGE_BROADCAST_BUNDLE, getString(messageId))
      action = Const.MESSAGE_BROADCAST_ACTION
    })
}