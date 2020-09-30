package net.ambitious.android.sharebookmarks.data.local.item

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.Const.OwnerType

@Dao
interface ItemDao {
  @Insert
  suspend fun insert(items: Item): Long

  @Insert
  suspend fun insertAll(vararg items: Item)

  @Update
  suspend fun update(items: Item)

  @Query("UPDATE items SET active = 0 WHERE id IN (:itemId)")
  suspend fun delete(vararg itemId: Long)

  @Query("SELECT * FROM items WHERE parent_id = :parentId AND active = 1 ORDER BY `order`")
  suspend fun getItems(parentId: Long): List<Item>

  @Query("SELECT * FROM items WHERE url IS NULL AND id != :selfId AND active = 1 AND owner_type = 0 ORDER BY `order`")
  suspend fun getFolderItems(selfId: Long): List<Item>

  @Query("SELECT * FROM items WHERE id = :itemId AND active = 1")
  suspend fun getItem(itemId: Long): Item?

  @Query("SELECT MAX(`order`) FROM items WHERE parent_id = :parentId AND active = 1")
  suspend fun getMaxOrder(parentId: Long): Int?

  @Query("UPDATE items SET parent_id = :parentId, `order` = :order WHERE id = :selfId")
  suspend fun move(selfId: Long, parentId: Long, order: Int)

  @Query("UPDATE items SET `order` = :order WHERE id = :selfId")
  suspend fun orderUpdate(selfId: Long, order: Int)

  @Query("SELECT owner_type FROM items WHERE id = :parentId")
  suspend fun getParentOwnerType(parentId: Long): Int?

  @Query("UPDATE items SET ogp_url = :ogpUrl WHERE id = :id")
  suspend fun updateOgpImages(ogpUrl: String?, id: Long)

  // 以下は同期で利用している

  @Query("SELECT remote_id FROM items WHERE remote_id IS NOT NULL AND active = 0")
  suspend fun getDeleteItems(): List<Long>

  @Query("SELECT * FROM items WHERE active = 1 ORDER BY parent_id, `order`, upserted")
  suspend fun getAllItems(): List<Item>

  @Query("SELECT * FROM items WHERE remote_id = :remoteId")
  suspend fun getSaveRemoteIdItem(remoteId: Long): Item?

  @Query("UPDATE items SET remote_id = :remoteId WHERE id = :id")
  suspend fun updateRemoteId(id: Long, remoteId: Long)

  @Query("SELECT remote_id FROM items WHERE id = (SELECT parent_id FROM items WHERE id = :id)")
  suspend fun getParentRemoteId(id: Long): Long?

  @Query("DELETE FROM items WHERE active = 0")
  suspend fun forceDelete()

  @Query("UPDATE items SET owner_type = :ownerType WHERE parent_id = :parentId")
  suspend fun updateOwnerType(ownerType: Int, parentId: Long)

  @Query("SELECT * FROM items WHERE active = 1 AND owner_type != 0")
  suspend fun getShareFolders(): List<Item>

  @Query("SELECT * FROM items WHERE parent_id = :parentId AND active = 1 AND url IS NULL")
  suspend fun getFolders(parentId: Long): List<Item>

  @Query("DELETE FROM items WHERE remote_id = 0")
  suspend fun deleteFirstItems()
}
