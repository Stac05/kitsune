package com.kitsune.app.reader

import android.content.Context
import android.net.Uri
import coil.ImageLoader
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import com.kitsune.app.data.repository.ReaderRepository
import okio.buffer
import okio.source

/**
 * Model data untuk Coil agar bisa memuat gambar langsung dari dalam file CBZ.
 */
data class CbzPageModel(
    val chapterUri: Uri,
    val entryPath: String
)

/**
 * Fetcher kustom untuk Coil yang melayani pemuatan gambar dari entri ZIP/CBZ.
 */
class CbzImageFetcher(
    private val context: Context,
    private val model: CbzPageModel,
    private val readerRepository: ReaderRepository
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        val inputStream = readerRepository.getPageStream(model.chapterUri, model.entryPath)
            ?: return null

        return SourceResult(
            source = ImageSource(
                source = inputStream.source().buffer(),
                context = context
            ),
            mimeType = null,
            dataSource = DataSource.DISK
        )
    }

    class Factory(
        private val context: Context,
        private val readerRepository: ReaderRepository
    ) : Fetcher.Factory<CbzPageModel> {
        override fun create(data: CbzPageModel, options: Options, imageLoader: ImageLoader): Fetcher {
            return CbzImageFetcher(context, data, readerRepository)
        }
    }
}
