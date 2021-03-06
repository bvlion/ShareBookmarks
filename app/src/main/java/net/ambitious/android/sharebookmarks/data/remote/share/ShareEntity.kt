package net.ambitious.android.sharebookmarks.data.remote.share

import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
object ShareEntity {
  @Keep
  data class ShareList(val shares: List<Share>) {
    data class Share(
      @Json(name = "remote_id") val remoteId: Long,
      @Json(name = "folder_id") val serverFolderId: Long,
      @Json(name = "user_email") val userEmail: String,
      @Json(name = "owner_type") val ownerType: Int,
      val updated: String
    )
  }

  @Keep
  data class PostShare(
    @Json(name = "local_id") val id: Long,
    @Json(name = "remote_id") val remoteId: Long?,
    @Json(name = "folder_id") val serverFolderId: Long,
    @Json(name = "user_email") val userEmail: String,
    @Json(name = "owner_type") val ownerType: Int,
    val updated: String
  )

  @Keep
  data class PostResponse(val shares: List<Share>) {
    data class Share(
      @Json(name = "local_id") val id: Long,
      @Json(name = "remote_id") val remoteId: Long
    )
  }

  @Keep
  data class DeleteShare(@Json(name = "delete_id") val remoteId: Long)

  @Keep
  data class DeleteResponse(@Json(name = "delete_count") val count: Int)
}