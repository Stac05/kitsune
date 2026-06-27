package com.kitsune.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.kitsune.app.data.repository.ReaderRepository
import com.kitsune.app.reader.CbzImageFetcher
import com.kitsune.app.reader.CbzParser

class KitsuneApplication : Application(), ImageLoaderFactory {

    lateinit var readerRepository: ReaderRepository
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Initialize dependencies for ImageLoader
        val cbzParser = CbzParser(this)
        readerRepository = ReaderRepository(cbzParser)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(CbzImageFetcher.Factory(this@KitsuneApplication, readerRepository))
            }
            .build()
    }
}
