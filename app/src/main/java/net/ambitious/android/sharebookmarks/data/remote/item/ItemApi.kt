package net.ambitious.android.sharebookmarks.data.remote.item

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT

interface ItemApi {
  @GET("items/list")
  suspend fun getItems(): ItemEntity.ItemList

  @POST("items/save")
  suspend fun postItems(@Body postData: List<ItemEntity.PostItem>): ItemEntity.PostResponse

  @PUT("items/parents")
  suspend fun parentSetItems(@Body parentItems: List<ItemEntity.ParentSet>): ItemEntity.ParentSetResponse

  @HTTP(method = "DELETE", path = "items/delete", hasBody = true)
  suspend fun deleteItems(@Body deleteIds: List<ItemEntity.DeleteShare>): ItemEntity.DeleteResponse
}