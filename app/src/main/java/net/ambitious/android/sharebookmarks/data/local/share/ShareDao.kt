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

  // 以下は同期で利用している

  @Query("SELECT remote_id FROM shares WHERE remote_id IS NOT NULL AND active = 0")
  suspend fun getDeleteShares(): List<Long>

  @Query("SELECT * FROM shares WHERE active = 1")
  suspend fun getAllShares(): List<Share>

  @Query("UPDATE shares SET remote_id = :remoteId WHERE id = :id")
  suspend fun updateRemoteId(id: Long, remoteId: Long)

  @Query("DELETE FROM shares WHERE active = 0")
  suspend fun forceDelete()
}