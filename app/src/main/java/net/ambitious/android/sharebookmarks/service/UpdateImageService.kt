package net.ambitious.android.sharebookmarks.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.local.item.ItemDao
import net.ambitious.android.sharebookmarks.data.remote.etc.EtcApi
import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.NotificationUtils
import net.ambitious.android.sharebookmarks.util.OperationUtils
import org.koin.android.ext.android.inject

class UpdateImageService : Service() {

  private val itemDao: ItemDao by inject()
  private val etcApi: EtcApi by inject()

  private val job = SupervisorJob()
  private val scope = CoroutineScope(Dispatchers.Main + job)

  override fun onBind(intent: Intent?) = Binder()

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY.apply {
    if (intent == null) {
      return@apply
    }

    startForeground(
      Const.NotificationService.IMAGE_UPDATE_ID,
      NotificationUtils.getHomeSituationNotification(
        this@UpdateImageService,
        R.string.notification_image_update_message
      )
    )
    scope.launch {
      intent.extras?.let {
        if (it.getBoolean(PARAM_ITEM_ALL)) {
          updateThumbnail(this, true)
        } else {
          val url = it.getString(PARAM_ITEM_URL)
          if (url == null) {
            updateThumbnail(this, false)
          } else {
            itemDao.updateOgpImages(
              OperationUtils.getOgpImage(url, etcApi) ?: "",
              it.getLong(PARAM_ITEM_ID)
            )
          }
        }
        sendBroadcast(Intent(Const.IMAGE_UPLOAD_BROADCAST_ACTION).apply {
          putExtra(Const.IMAGE_UPLOAD_BROADCAST_BUNDLE, it.getBoolean(PARAM_ITEM_ALL))
        })
      }
      stopSelf()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    job.cancel()
  }

  private suspend fun updateThumbnail(coroutineScope: CoroutineScope, isAll: Boolean) {
    val items = ArrayList(itemDao.getAllItems())
    val dropCount = 10
    do {
      items.take(dropCount).map {
        coroutineScope.async {
          if (it.url == null) {
            return@async
          }
          // 全件取得でない場合は既にサムネイルの URL があればスキップする
          if (!isAll && it.ogpUrl != null) {
            return@async
          }
          withContext(Dispatchers.IO) {
            itemDao.updateOgpImages(
              OperationUtils.getOgpImage(it.url, etcApi) ?: "",
              it.id!!
            )
          }
        }
      }.forEach {
        it.await()
      }
      repeat(dropCount) {
        if (items.isNotEmpty()) {
          items.removeAt(0)
        }
      }
    } while (items.isNotEmpty())
  }

  companion object {
    const val PARAM_ITEM_ID = "param_item_id"
    const val PARAM_ITEM_URL = "param_item_url"
    const val PARAM_ITEM_ALL = "param_item_all"
  }
}