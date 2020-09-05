package net.ambitious.android.sharebookmarks.data.local.share

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.joda.time.DateTime
import java.io.Serializable

@Entity(
    tableName = "shares",
    indices = [
      Index(
          value = ["folder_id"],
          name = "index_shares"
      )
    ]
)
data class Share(
  /** ローカル ID */
  @PrimaryKey(autoGenerate = true) var id: Long? = null,
  /** サーバー側の ID */
  @ColumnInfo(name = "remote_id") val remoteId: Long?,
  /** シェア対象フォルダ ID */
  @ColumnInfo(name = "folder_id") val folderId: Long,
  /** シェア対象 Email */
  @ColumnInfo(name = "user_email") val userEmail: String,
  /** 本人と保持・編集可・閲覧のみフラグ */
  @ColumnInfo(name = "owner_type") val ownerType: Int,
  /** 有効フラグ */
  val active: Int = 1,
  /** 作成・更新日時 */
  val upserted: DateTime = DateTime()
) : Serializable