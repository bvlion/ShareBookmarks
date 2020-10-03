package net.ambitious.android.sharebookmarks.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ImageUploadEndBroadcastReceiver(private val block: () -> Unit) : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    block()
  }
}