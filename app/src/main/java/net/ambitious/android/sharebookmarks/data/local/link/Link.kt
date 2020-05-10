package net.ambitious.android.sharebookmarks.data.local.link

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Link (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "remote_id") val remoteId: Int?,
    val title: String?,
    val url: String?,
    @ColumnInfo(name = "owner_id") val ownerId: Int?,
    val order: Int,
    @ColumnInfo(name = "owner_type") val ownerType: Int?
)