package com.kitsune.app.domain.model

/**
 * Representasi model data komik di domain layer.
 */
data class Comic(
    val title: String,
    val relativePath: String,
    val coverUri: String?,
    val lastModified: Long
)
