package net.ambitious.android.sharebookmarks.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.ambitious.android.sharebookmarks.util.Const

class MessageBroadcastReceiver(private val block: (message: String) -> Unit) : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    intent?.let {
      it.extras?.let { extras ->
        val message = extras.getString(Const.MESSAGE_BROADCAST_BUNDLE)
        if (!message.isNullOrEmpty()) {
          block(message)
        }
      }
    }
  }
}