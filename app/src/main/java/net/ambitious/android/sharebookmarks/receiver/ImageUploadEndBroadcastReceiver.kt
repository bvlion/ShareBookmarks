package net.ambitious.android.sharebookmarks.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.ambitious.android.sharebookmarks.util.Const

class ImageUploadEndBroadcastReceiver(private val block: (isAll: Boolean) -> Unit) :
  BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    intent?.let {
      it.extras?.let { extras ->
        block(extras.getBoolean(Const.IMAGE_UPLOAD_BROADCAST_BUNDLE))
      }
    }
  }
}