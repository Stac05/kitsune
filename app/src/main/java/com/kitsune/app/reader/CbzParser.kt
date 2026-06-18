package com.kitsune.app.reader

import android.content.Context
import android.net.Uri
import com.kitsune.app.core.NaturalOrderComparator
import com.kitsune.app.domain.model.Page
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * Parser untuk membaca isi file CBZ (Zip) tanpa mengekstraknya ke storage.
 */
class CbzParser(private val context: Context) {

    private val naturalOrderComparator = NaturalOrderComparator()
    private val allowedExtensions = setOf("jpg", "jpeg", "png", "webp")

    /**
     * Mengambil daftar halaman dari file CBZ.
     * Hanya membaca entri zip tanpa memuat data gambar ke memori.
     */
    suspend fun getPages(chapterUri: Uri): List<Page> = withContext(Dispatchers.IO) {
        val pages = mutableListOf<RawPageEntry>()
        
        try {
            context.contentResolver.openInputStream(chapterUri)?.use { inputStream ->
                ZipInputStream(inputStream).use { zipStream ->
                    var entry = zipStream.nextEntry
                    while (entry != null) {
                        if (!entry.isDirectory) {
                            val name = entry.name
                            val extension = name.substringAfterLast('.', "").lowercase()
                            
                            if (extension in allowedExtensions) {
                                pages.add(RawPageEntry(name))
                            }
                        }
                        zipStream.closeEntry()
                        entry = zipStream.nextEntry
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }

        // Urutkan entri menggunakan Natural Sorting
        pages.sortWith { e1, e2 ->
            naturalOrderComparator.compare(e1.name, e2.name)
        }

        // Konversi ke model Page dengan nomor halaman
        pages.mapIndexed { index, entry ->
            Page(
                pageNumber = index + 1,
                imageName = entry.name.substringAfterLast('/'),
                entryPath = entry.name
            )
        }
    }

    /**
     * Membuka InputStream untuk entri spesifik di dalam file CBZ.
     * PENTING: ZipInputStream tidak mendukung random access dengan efisien.
     * Kita harus melakukan iterasi sampai menemukan entri yang dimaksud.
     */
    fun getEntryInputStream(chapterUri: Uri, entryPath: String): InputStream? {
        val inputStream = context.contentResolver.openInputStream(chapterUri) ?: return null
        val zipStream = ZipInputStream(inputStream)
        
        var entry = zipStream.nextEntry
        while (entry != null) {
            if (entry.name == entryPath) {
                // Jangan menutup zipStream di sini karena pemanggil butuh stream-nya.
                // Namun ini berisiko leak jika tidak dikelola dengan baik.
                return zipStream
            }
            zipStream.closeEntry()
            entry = zipStream.nextEntry
        }
        
        zipStream.close()
        return null
    }

    private data class RawPageEntry(val name: String)
}
