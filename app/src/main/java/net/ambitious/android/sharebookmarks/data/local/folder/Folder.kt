package net.ambitious.android.sharebookmarks.data.local.folder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Folder (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "remote_id") val remoteId: Int?,
    @ColumnInfo(name = "owner_id") val ownerId: Int?,
    val name: String?,
    val order: Int,
    @ColumnInfo(name = "parent_id") val parentId: Int?
)