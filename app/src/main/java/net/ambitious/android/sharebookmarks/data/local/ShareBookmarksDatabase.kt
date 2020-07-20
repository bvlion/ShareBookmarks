package net.ambitious.android.sharebookmarks.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.ambitious.android.sharebookmarks.data.local.folder.Folder
import net.ambitious.android.sharebookmarks.data.local.folder.FolderDao
import net.ambitious.android.sharebookmarks.data.local.link.Link
import net.ambitious.android.sharebookmarks.data.local.link.LinkDao

@Database(
    entities = [
        Folder::class,
        Link::class
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

        fun getInstance(context: Context): ShareBookmarksDatabase {
            if (instance != null) return instance!!

            synchronized(lock) {
                if (instance == null) {
                    instance =
                        buildInstance(
                            context
                        )
                }
                return instance!!
            }
        }

        private fun buildInstance(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ShareBookmarksDatabase::class.java,
                DB_NAME
            )
                .build()
    }

    abstract fun folderDao(): FolderDao
    abstract fun linkDao(): LinkDao
}