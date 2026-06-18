package com.kitsune.app.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity untuk menyimpan progres membaca komik.
 * Menggunakan relative path sebagai identifier unik.
 */
@Entity(
    tableName = "reading_progress",
    indices = [Index(value = ["comicRelativePath"], unique = true)]
)
data class ReadingProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val comicRelativePath: String,
    val chapterRelativePath: String,
    val pageNumber: Int,
    val totalPages: Int,
    val lastReadAt: Long = System.currentTimeMillis()
)
