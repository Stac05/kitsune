package com.kitsune.app.data.repository

import android.net.Uri
import com.kitsune.app.database.dao.ComicDao
import com.kitsune.app.database.entity.ComicEntity
import com.kitsune.app.domain.model.Comic
import com.kitsune.app.scanner.ComicScanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository untuk mengelola operasi pemindaian media.
 * Menghubungkan Scanner Engine dengan domain layer dan database cache.
 */
class ScannerRepository(
    private val comicScanner: ComicScanner,
    private val comicDao: ComicDao
) {

    /**
     * Aliran data komik yang tersimpan di database.
     */
    val allComics: Flow<List<Comic>> = comicDao.getAllComics().map { entities ->
        entities.map { it.toDomain() }
    }

    /**
     * Melakukan pemindaian inkremental pada folder komik.
     * Membandingkan data di filesystem dengan cache database berdasarkan lastModified.
     */
    suspend fun performIncrementalScan(rootUri: Uri) {
        // 1. Ambil cache dari database
        val cachedComics = comicDao.getAllComicsSync()
        val cacheMap = cachedComics.associateBy { it.relativePath }

        // 2. Scan filesystem menggunakan Hybrid SAF
        val scannedComics = comicScanner.scanComics(rootUri) { path, lastMod ->
            // Callback: jika lastModified sama, gunakan cover dari cache agar tidak scan fisik lagi
            val cached = cacheMap[path]
            if (cached != null && cached.lastModified == lastMod) {
                cached.coverUri
            } else {
                null
            }
        }

        // 3. Deteksi perubahan
        val scannedPaths = scannedComics.map { it.relativePath }.toSet()
        
        // Komik yang harus dihapus (ada di cache tapi tidak ada di filesystem)
        val toDelete = cachedComics
            .filter { it.relativePath !in scannedPaths }
            .map { it.relativePath }

        // Komik yang baru atau diperbarui
        val toInsert = scannedComics.map { it.toEntity() }

        // 4. Update database dalam satu transaksi
        if (toInsert.isNotEmpty() || toDelete.isNotEmpty()) {
            comicDao.updateLibrary(toInsert, toDelete)
        }
    }

    /**
     * Extension: Konversi Entity ke Domain Model
     */
    private fun ComicEntity.toDomain() = Comic(
        title = title,
        relativePath = relativePath,
        coverUri = coverUri,
        lastModified = lastModified
    )

    /**
     * Extension: Konversi Domain Model ke Entity
     */
    private fun Comic.toEntity() = ComicEntity(
        title = title,
        relativePath = relativePath,
        coverUri = coverUri,
        lastModified = lastModified
    )
}
