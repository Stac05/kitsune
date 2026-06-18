package com.kitsune.app.data.repository

import com.kitsune.app.database.dao.ReadingProgressDao
import com.kitsune.app.database.entity.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository untuk mengelola data progres membaca.
 */
class ReadingProgressRepository(private val readingProgressDao: ReadingProgressDao) {

    /**
     * Mendapatkan aliran data progres untuk sebuah komik.
     */
    fun getProgressByComic(comicPath: String): Flow<ReadingProgressEntity?> {
        return readingProgressDao.getProgressByComic(comicPath)
    }

    /**
     * Mendapatkan progres komik secara sinkron (suspend).
     */
    suspend fun getProgressByComicSync(comicPath: String): ReadingProgressEntity? {
        return readingProgressDao.getProgressByComicSync(comicPath)
    }

    /**
     * Menyimpan atau memperbarui progres membaca.
     */
    suspend fun saveProgress(
        comicRelativePath: String,
        chapterRelativePath: String,
        pageNumber: Int,
        totalPages: Int
    ) {
        val progress = ReadingProgressEntity(
            comicRelativePath = comicRelativePath,
            chapterRelativePath = chapterRelativePath,
            pageNumber = pageNumber,
            totalPages = totalPages,
            lastReadAt = System.currentTimeMillis()
        )
        // Karena OnConflictStrategy.REPLACE di Dao, ini akan memperbarui entri yang ada
        // berdasarkan indeks unik comicRelativePath.
        readingProgressDao.saveProgress(progress)
    }

    /**
     * Menghapus progres membaca untuk komik tertentu.
     */
    suspend fun deleteProgress(comicPath: String) {
        readingProgressDao.deleteProgress(comicPath)
    }
}
