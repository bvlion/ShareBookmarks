package net.ambitious.android.sharebookmarks.util

import net.ambitious.android.sharebookmarks.data.local.item.Item

object Const {
  const val STORE_URL = "net.ambitious.android.sharebookmarks"
  const val GOOGLE_FAVICON_URL = "https://www.google.com/s2/favicons?domain="

  val HOME_FOLDER = Item(0, null, 0, "Home", null, 0, 0)

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
      Item(null, 0, 0, "Google", "https://www.google.com/", 1, OwnerType.OWNER.value),
      Item(null, 0, 0, "Yahoo", "https://www.yahoo.co.jp/", 2, OwnerType.OWNER.value),
      Item(null, 0, 0, "フォルダ", null, 3, OwnerType.OWNER.value)
  )

  object NotificationService {
    const val DATA_UPDATE_ID = 23487452

    const val NOTICE_CHANNEL = "Channels.NOTICE_CHANNEL"
    const val DATA_UPDATE_CHANNEL = "Channels.DATA_UPDATE_CHANNEL"
  }

  const val MESSAGE_BROADCAST_ACTION = "message_broadcast_action"
  const val MESSAGE_BROADCAST_BUNDLE = "message_broadcast_bundle"
}