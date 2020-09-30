package net.ambitious.android.sharebookmarks.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.ambitious.android.sharebookmarks.data.local.item.ItemDao
import net.ambitious.android.sharebookmarks.data.remote.etc.EtcApi
import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.NotificationUtils
import net.ambitious.android.sharebookmarks.util.OperationUtils
import org.koin.android.ext.android.inject

class UpdateImageService : Service() {

  private val itemDao: ItemDao by inject()
  private val etcApi: EtcApi by inject()

  override fun onBind(intent: Intent?) = Binder()

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY.apply {
    if (intent == null) {
      return@apply
    }

    startForeground(
        Const.NotificationService.DATA_UPDATE_ID,
        NotificationUtils.getHomeSituationNotification(this@UpdateImageService)
    )

    GlobalScope.launch {
      intent.extras?.let {
        itemDao.updateOgpImages(
            OperationUtils.getOgpImage(it.getString(PARAM_ITEM_URL) ?: return@let, etcApi),
            it.getLong(PARAM_ITEM_ID)
        )
      }
      stopSelf()
    }
  }

  companion object {
    const val PARAM_ITEM_ID = "param_item_id"
    const val PARAM_ITEM_URL = "param_item_url"
  }
}