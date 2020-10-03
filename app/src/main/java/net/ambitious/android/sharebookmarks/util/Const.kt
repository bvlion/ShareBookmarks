package net.ambitious.android.sharebookmarks.util

import net.ambitious.android.sharebookmarks.data.local.item.Item

object Const {
  const val STORE_URL = "net.ambitious.android.sharebookmarks"
  const val GOOGLE_FAVICON_URL = "https://www.google.com/s2/favicons?domain="

  val HOME_FOLDER = Item(0, null, 0, "Home", null, null, 0, 0)

  enum class OwnerType(val value: Int) {
    OWNER(0),
    EDITABLE(1),
    READONLY(2)
  }

  enum class ItemType(val value: Int) {
    ITEM(0),
    FOLDER(1)
  }

  enum class StartFolderType(val value: String) {
    ROOT("root"),
    LAST("last"),
    TARGET("target")
  }

  val INITIALIZE_DB = arrayOf(
      arrayOf("Google", "https://www.google.com/"),
      arrayOf("Yahoo", "https://www.yahoo.co.jp/"),
      arrayOf("フォルダ", null)
  )

  object NotificationService {
    const val DATA_UPDATE_ID = 23487452
    const val IMAGE_UPDATE_ID = 23487453

    const val NOTICE_CHANNEL = "Channels.NOTICE_CHANNEL"
    const val DATA_UPDATE_CHANNEL = "Channels.DATA_UPDATE_CHANNEL"
  }

  const val MESSAGE_BROADCAST_ACTION = "message_broadcast_action"
  const val MESSAGE_BROADCAST_BUNDLE = "message_broadcast_bundle"

  const val IMAGE_UPLOAD_BROADCAST_ACTION = "image_upload_broadcast_action"
}