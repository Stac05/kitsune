package com.kitsune.app.domain.model

import com.kitsune.app.database.entity.ReadingProgressEntity

/**
 * Model data gabungan untuk menampilkan progres terakhir di halaman Local.
 */
data class LastReadComic(
    val comic: Comic,
    val progress: ReadingProgressEntity
)
