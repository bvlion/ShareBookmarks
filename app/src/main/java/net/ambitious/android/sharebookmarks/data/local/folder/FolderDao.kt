package net.ambitious.android.sharebookmarks.data.local.folder

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

@Dao
interface FolderDao {
  @Insert
  fun insertAll(vararg folders: Folder)

  @Delete
  fun delete(folder: Folder)
}