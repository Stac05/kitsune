package com.kitsune.app.data.repository

import com.kitsune.app.database.dao.ComicDao
import com.kitsune.app.database.dao.ReadingProgressDao
import com.kitsune.app.database.entity.ReadingProgressEntity
import com.kitsune.app.domain.model.Comic
import com.kitsune.app.domain.model.LastReadComic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Repository untuk mengelola data progres membaca.
 */
class ReadingProgressRepository(
    private val readingProgressDao: ReadingProgressDao,
    private val comicDao: ComicDao
) {

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
        readingProgressDao.saveProgress(progress)
    }

    /**
     * Menghapus progres membaca untuk komik tertentu.
     */
    suspend fun deleteProgress(comicPath: String) {
        readingProgressDao.deleteProgress(comicPath)
    }

    /**
     * Mendapatkan progres membaca terbaru yang digabungkan dengan data komik.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLatestReadComic(): Flow<LastReadComic?> {
        return readingProgressDao.getLatestProgress().flatMapLatest { progress ->
            if (progress == null) {
                flowOf(null)
            } else {
                val comicEntity = comicDao.getComicByPath(progress.comicRelativePath)
                if (comicEntity != null) {
                    flowOf(
                        LastReadComic(
                            comic = Comic(
                                title = comicEntity.title,
                                relativePath = comicEntity.relativePath,
                                coverUri = comicEntity.coverUri,
                                lastModified = comicEntity.lastModified
                            ),
                            progress = progress
                        )
                    )
                } else {
                    flowOf(null)
                }
            }
        }
    }
}
