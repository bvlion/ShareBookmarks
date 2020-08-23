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
}