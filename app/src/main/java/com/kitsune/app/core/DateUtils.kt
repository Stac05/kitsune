package com.kitsune.app.core

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility untuk pemformatan tanggal yang konsisten di seluruh aplikasi Kitsune.
 */
object DateUtils {

    private const val DEFAULT_DATE_FORMAT = "dd MMMM yyyy"

    /**
     * Mengonversi timestamp (milidetik) menjadi string tanggal terformat.
     * @param timestamp Waktu dalam milidetik.
     * @param locale Locale untuk internasionalisasi (default: Locale.getDefault()).
     * @return String tanggal terformat atau null jika timestamp tidak valid (<= 0).
     */
    fun formatTimestamp(
        timestamp: Long,
        locale: Locale = Locale.getDefault(),
        pattern: String = DEFAULT_DATE_FORMAT
    ): String? {
        if (timestamp <= 0) return null
        
        return try {
            val date = Date(timestamp)
            val sdf = SimpleDateFormat(pattern, locale)
            sdf.format(date)
        } catch (e: Exception) {
            null
        }
    }
}
