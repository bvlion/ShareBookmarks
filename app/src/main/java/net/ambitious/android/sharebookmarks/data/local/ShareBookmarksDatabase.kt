package net.ambitious.android.sharebookmarks.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.data.local.item.ItemDao
import net.ambitious.android.sharebookmarks.data.local.share.Share
import net.ambitious.android.sharebookmarks.data.local.share.ShareDao

@Database(
  entities = [
    Item::class,
    Share::class
  ],
  version = 1
)
@TypeConverters(DateTimeConverter::class)
abstract class ShareBookmarksDatabase : RoomDatabase() {
  companion object {

    private const val DB_NAME = "ShareBookmarks.db"

    fun createInstance(context: Context) =
      Room.databaseBuilder(
        context,
        ShareBookmarksDatabase::class.java,
        DB_NAME
      ).build()
  }

  abstract fun itemDao(): ItemDao

  abstract fun shareDao(): ShareDao
}