package com.kitsune.app.scanner

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.kitsune.app.core.NaturalOrderComparator
import com.kitsune.app.domain.model.Comic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Engine untuk melakukan scanning folder komik secara offline.
 * Menggunakan Hybrid SAF (DocumentFile) untuk navigasi direktori.
 */
class ComicScanner(private val context: Context) {

    private val naturalOrderComparator = NaturalOrderComparator()
    private val allowedImageExtensions = listOf("jpg", "jpeg", "png", "webp")

    /**
     * Memindai folder 'Comics' di dalam root URI yang diberikan.
     * Menggunakan callback [getExistingCover] untuk mendukung incremental scan:
     * Jika lastModified folder sama dengan cache, scanner bisa melewati pencarian cover fisik.
     */
    suspend fun scanComics(
        rootUri: Uri,
        getExistingCover: (relativePath: String, lastModified: Long) -> String? = { _, _ -> null }
    ): List<Comic> = withContext(Dispatchers.IO) {
        val rootDoc = DocumentFile.fromTreeUri(context, rootUri) ?: return@withContext emptyList()
        val comicsFolder = rootDoc.findFile("Comics") ?: return@withContext emptyList()
        
        if (!comicsFolder.isDirectory) return@withContext emptyList()

        val comicFolders = comicsFolder.listFiles()
            .filter { it.isDirectory }
            .sortedWith { f1, f2 -> 
                naturalOrderComparator.compare(f1.name ?: "", f2.name ?: "") 
            }

        comicFolders.mapNotNull { folder ->
            val title = folder.name ?: return@mapNotNull null
            val relativePath = "Comics/$title"
            val currentLastModified = folder.lastModified()
            
            // Cek apakah ada cover di cache yang masih valid
            val cachedCover = getExistingCover(relativePath, currentLastModified)
            
            val coverUri = cachedCover ?: findCover(folder)?.toString()
            
            Comic(
                title = title,
                relativePath = relativePath,
                coverUri = coverUri,
                lastModified = currentLastModified
            )
        }
    }

    private fun findCover(folder: DocumentFile): Uri? {
        return folder.listFiles().find { file ->
            val fileName = file.name?.lowercase() ?: ""
            allowedImageExtensions.any { ext -> fileName == "cover.$ext" }
        }?.uri
    }
}
