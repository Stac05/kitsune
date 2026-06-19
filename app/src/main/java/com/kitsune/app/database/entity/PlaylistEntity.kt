package com.kitsune.app.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity untuk menyimpan metadata playlist.
 */
@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)
