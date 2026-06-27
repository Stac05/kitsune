package com.kitsune.app.data.repository

import com.kitsune.app.database.dao.BookmarkDao
import com.kitsune.app.database.entity.BookmarkComicEntity
import com.kitsune.app.database.entity.BookmarkEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * Repository untuk mengelola Bookmark.
 */
class BookmarkRepository(private val bookmarkDao: BookmarkDao) {

    /**
     * Mendapatkan semua bookmark beserta jumlah komiknya.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllBookmarksWithCount(): Flow<List<BookmarkWithCount>> {
        return bookmarkDao.getAllBookmarks().flatMapLatest { bookmarks ->
            if (bookmarks.isEmpty()) {
                flowOf(emptyList())
            } else {
                val flows = bookmarks.map { bookmark ->
                    bookmarkDao.getComicCountInBookmark(bookmark.id).map { count ->
                        BookmarkWithCount(bookmark, count)
                    }
                }
                combine(flows) { it.toList() }
            }
        }
    }

    suspend fun getBookmarkById(id: Long): BookmarkEntity? {
        return bookmarkDao.getBookmarkById(id)
    }

    fun getComicsInBookmark(bookmarkId: Long): Flow<List<String>> {
        return bookmarkDao.getComicsInBookmark(bookmarkId)
    }

    /**
     * Mendapatkan seluruh jalur relatif komik yang ada di kategori bookmark manapun.
     */
    fun getAllBookmarkedComics(): Flow<List<String>> {
        return bookmarkDao.getAllBookmarkedComics()
    }

    suspend fun createBookmark(name: String): Long {
        return bookmarkDao.insertBookmark(BookmarkEntity(name = name))
    }

    suspend fun renameBookmark(id: Long, newName: String) {
        bookmarkDao.renameBookmark(id, newName)
    }

    suspend fun deleteBookmark(id: Long) {
        bookmarkDao.deleteBookmark(id)
    }

    /**
     * Menghapus banyak bookmark sekaligus.
     */
    suspend fun deleteBookmarks(ids: List<Long>) {
        bookmarkDao.deleteBookmarks(ids)
    }

    suspend fun addComicToBookmark(bookmarkId: Long, comicPath: String) {
        bookmarkDao.addComicToBookmark(
            BookmarkComicEntity(bookmarkId = bookmarkId, comicRelativePath = comicPath)
        )
    }

    /**
     * REVISION 5.2: Menambahkan banyak komik ke banyak bookmark sekaligus secara batch.
     */
    suspend fun addComicsToBookmarks(bookmarkIds: List<Long>, comicPaths: List<String>) {
        val entities = bookmarkIds.flatMap { bookmarkId ->
            comicPaths.map { path ->
                BookmarkComicEntity(bookmarkId = bookmarkId, comicRelativePath = path)
            }
        }
        if (entities.isNotEmpty()) {
            bookmarkDao.addComicsToBookmarks(entities)
        }
    }

    suspend fun removeComicFromBookmark(bookmarkId: Long, comicPath: String) {
        bookmarkDao.removeComicFromBookmark(bookmarkId, comicPath)
    }

    /**
     * Menghapus banyak komik dari bookmark tertentu sekaligus.
     */
    suspend fun removeComicsFromBookmark(bookmarkId: Long, comicPaths: List<String>) {
        bookmarkDao.removeComicsFromBookmark(bookmarkId, comicPaths)
    }

    fun isComicInBookmark(bookmarkId: Long, comicPath: String): Flow<Boolean> {
        return bookmarkDao.isComicInBookmark(bookmarkId, comicPath)
    }

    /**
     * Mendapatkan daftar ID bookmark yang berisi komik tertentu.
     */
    fun getBookmarkIdsForComic(comicPath: String): Flow<List<Long>> {
        return bookmarkDao.getBookmarkIdsForComic(comicPath)
    }
}

data class BookmarkWithCount(
    val bookmark: BookmarkEntity,
    val count: Int
)
