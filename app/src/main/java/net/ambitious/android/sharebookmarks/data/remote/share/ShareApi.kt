package net.ambitious.android.sharebookmarks.data.remote.share


import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface ShareApi {
  @GET("shares/list")
  suspend fun getShares(): ShareEntity.ShareList

  @POST("shares/save")
  suspend fun postShares(@Body postData: List<ShareEntity.PostShare>): ShareEntity.PostResponse

  @DELETE("shares/delete")
  suspend fun deleteShares(@Body deleteIds: List<ShareEntity.DeleteShare>): ShareEntity.DeleteResponse
}