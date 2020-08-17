package net.ambitious.android.sharebookmarks.data.local.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "items",
    indices = [
      Index(
          value = ["parent_id", "order"],
          name = "index_items"
      )
    ]
)
data class Item(
  /** ローカル ID */
  @PrimaryKey(autoGenerate = true) var id: Long? = null,
  /** サーバー側の ID */
  @ColumnInfo(name = "remote_id") val remoteId: Long?,
  /** 親となるフォルダの ID（トップレベルの場合は 0） */
  @ColumnInfo(name = "parent_id") val parentId: Long,
  /** 名称 */
  val name: String,
  /** URL（フォルダーの場合は null） */
  val url: String?,
  /** 順序（parent_id ごとに一意） */
  val order: Int,
  /** 本人と保持・編集可・閲覧のみフラグ */
  @ColumnInfo(name = "owner_type") val ownerType: Int
) : Serializable
