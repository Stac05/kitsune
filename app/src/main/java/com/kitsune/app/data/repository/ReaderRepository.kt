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

    /**
     * Mendapatkan daftar halaman dari file CBZ berdasarkan URI.
     */
    suspend fun getPages(chapterUri: Uri): List<Page> {
        return cbzParser.getPages(chapterUri)
    }

    /**
     * Membuka InputStream untuk entri spesifik di dalam file CBZ.
     */
    fun getPageStream(chapterUri: Uri, entryPath: String): InputStream? {
        return cbzParser.getEntryInputStream(chapterUri, entryPath)
    }
}
