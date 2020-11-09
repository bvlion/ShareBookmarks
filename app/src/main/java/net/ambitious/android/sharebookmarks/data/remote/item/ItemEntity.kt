package net.ambitious.android.sharebookmarks.data.remote.item

import com.squareup.moshi.Json

object ItemEntity {
  data class ItemList(val items: List<Item>) {
    data class Item(
      @Json(name = "remote_id") val remoteId: Long,
      @Json(name = "remote_parent_id") val remoteParentId: Long,
      val name: String,
      val url: String?,
      val orders: Int,
      @Json(name = "owner_type") val ownerType: Int,
      val updated: String,
      val deleted: Boolean
    )
  }

  data class PostItem(
    @Json(name = "local_id") val id: Long,
    @Json(name = "remote_id") val remoteId: Long?,
    val name: String,
    val url: String?,
    val orders: Int,
    val updated: String
  )

  data class PostResponse(val items: List<Item>) {
    data class Item(
      @Json(name = "local_id") val id: Long,
      @Json(name = "remote_id") val remoteId: Long
    )
  }

  data class ParentSet(
    @Json(name = "remote_id") val remoteId: Long,
    @Json(name = "parent_id") val parentId: Long,
    @Json(name = "is_share_folder") val isSharedFolder: Boolean
  )

  data class ParentSetResponse(@Json(name = "result_count") val count: Int)

  data class DeleteShare(@Json(name = "delete_id") val remoteId: Long)

  data class DeleteResponse(@Json(name = "delete_count") val count: Int)
}