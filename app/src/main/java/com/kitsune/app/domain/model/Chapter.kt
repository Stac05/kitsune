package com.kitsune.app.domain.model

/**
 * Representasi model data chapter komik (file .cbz).
 */
data class Chapter(
    val name: String,
    val relativePath: String,
    val lastModified: Long
)
