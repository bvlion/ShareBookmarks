package net.ambitious.android.sharebookmarks.data.local.item

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDao {
  @Insert
  suspend fun insertAll(vararg items: Item)

  @Delete
  suspend fun delete(item: Item)

  @Query("SELECT * FROM items WHERE parent_id = :parentId ORDER BY `order`")
  suspend fun getItems(parentId: Long): List<Item>
}
