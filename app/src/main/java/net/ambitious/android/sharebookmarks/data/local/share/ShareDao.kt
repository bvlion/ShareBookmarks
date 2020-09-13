package net.ambitious.android.sharebookmarks.data.local.share

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ShareDao {
  @Insert
  suspend fun insertAll(vararg shares: Share)

  @Update
  suspend fun update(share: Share)

  @Query("UPDATE shares SET active = 0 WHERE id IN(:shareIds)")
  suspend fun delete(vararg shareIds: Long)

  @Query("SELECT * FROM shares WHERE folder_id = :folderId AND active = 1 ORDER BY id")
  suspend fun getShares(folderId: Long): List<Share>
}