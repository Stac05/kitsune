package com.kitsune.app.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity untuk menyimpan cache metadata komik hasil scanning.
 * Digunakan untuk mendukung Incremental Scan.
 */
@Entity(tableName = "comics")
data class ComicEntity(
    @PrimaryKey val relativePath: String,
    val title: String,
    val coverUri: String?,
    val lastModified: Long
)
