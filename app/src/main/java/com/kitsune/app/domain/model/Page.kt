package com.kitsune.app.domain.model

/**
 * Representasi satu halaman di dalam chapter (CBZ).
 */
data class Page(
    val pageNumber: Int,
    val imageName: String,
    val entryPath: String
)
