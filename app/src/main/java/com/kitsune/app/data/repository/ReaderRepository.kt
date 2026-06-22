package com.kitsune.app.data.repository

import android.net.Uri
import com.kitsune.app.domain.model.Page
import com.kitsune.app.reader.CbzParser
import java.io.InputStream

/**
 * Repository untuk mengelola logika pembacaan konten komik (CBZ).
 */
class ReaderRepository(
    private val cbzParser: CbzParser
) {
    // Memory Cache untuk daftar halaman guna menghindari parsing ulang ZIP yang mahal
    private val pageCache = mutableMapOf<String, List<Page>>()

    /**
     * Mendapatkan daftar halaman dari file CBZ berdasarkan URI.
     * Mendukung caching berbasis key (path + lastModified).
     */
    suspend fun getPages(chapterUri: Uri, cacheKey: String? = null): List<Page> {
        // 1. Cek cache jika key tersedia
        if (cacheKey != null) {
            pageCache[cacheKey]?.let { return it }
        }

        // 2. Parse ZIP jika cache miss
        val pages = cbzParser.getPages(chapterUri)

        // 3. Simpan ke cache jika parsing berhasil
        if (cacheKey != null && pages.isNotEmpty()) {
            pageCache[cacheKey] = pages
        }

        return pages
    }

    /**
     * Membuka InputStream untuk entri spesifik di dalam file CBZ.
     */
    fun getPageStream(chapterUri: Uri, entryPath: String): InputStream? {
        return cbzParser.getEntryInputStream(chapterUri, entryPath)
    }
}
