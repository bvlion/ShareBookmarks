package net.ambitious.android.sharebookmarks.data.local.link

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

@Dao
interface LinkDao {
  @Insert
  fun insertAll(vararg links: Link)

  @Delete
  fun delete(link: Link)
}