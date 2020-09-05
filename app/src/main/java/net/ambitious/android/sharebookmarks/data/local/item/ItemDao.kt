package net.ambitious.android.sharebookmarks.data.local.item

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItemDao {
  @Insert
  suspend fun insert(items: Item)

  @Insert
  suspend fun insertAll(vararg items: Item)

  @Update
  suspend fun update(items: Item)

  @Query("UPDATE items SET active = 0 WHERE id = :itemId")
  suspend fun delete(itemId: Long)

  @Query("SELECT * FROM items WHERE parent_id = :parentId AND active = 1 ORDER BY `order`")
  suspend fun getItems(parentId: Long): List<Item>

  @Query("SELECT * FROM items WHERE url IS NULL AND id != :selfId AND active = 1 ORDER BY `order`")
  suspend fun getFolderItems(selfId: Long): List<Item>

  @Query("SELECT * FROM items WHERE id = :itemId AND active = 1")
  suspend fun getItem(itemId: Long): Item?

  @Query("SELECT MAX(`order`) FROM items WHERE parent_id = :parentId AND active = 1")
  suspend fun getMaxOrder(parentId: Long): Int?

  @Query("UPDATE items SET parent_id = :parentId, `order` = :order WHERE id = :selfId")
  suspend fun move(selfId: Long, parentId: Long, order: Int)

  @Query("UPDATE items SET `order` = :order WHERE id = :selfId")
  suspend fun orderUpdate(selfId: Long, order: Int)
}
