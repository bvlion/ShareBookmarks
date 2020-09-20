package net.ambitious.android.sharebookmarks.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
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