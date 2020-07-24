package net.ambitious.android.sharebookmarks.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.data.local.item.ItemDao

@Database(
    entities = [
      Item::class
    ],
    version = 1
)
abstract class ShareBookmarksDatabase : RoomDatabase() {
  companion object {

    private const val DB_NAME = "ShareBookmarks.db"

    private var instance: ShareBookmarksDatabase? = null

    private val lock = Any()

    fun initialize(context: Context) =
      synchronized(lock) {
        instance =
          buildInstance(
              context
          )
      }

    fun getInstance(context: Context): ShareBookmarksDatabase =
      instance ?: synchronized(lock) {
        buildInstance(context)
            .also { instance = it }
      }

    private fun buildInstance(context: Context) =
      Room.databaseBuilder(
          context.applicationContext,
          ShareBookmarksDatabase::class.java,
          DB_NAME
      ).build()
  }

  abstract fun itemDao(): ItemDao
}