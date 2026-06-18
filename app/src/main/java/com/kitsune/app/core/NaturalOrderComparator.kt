package com.kitsune.app.core

import java.util.Comparator

/**
 * Comparator untuk melakukan Natural Sorting (mengurutkan angka secara numerik dalam string).
 * Contoh: "image_2" akan muncul sebelum "image_10".
 */
class NaturalOrderComparator : Comparator<String> {
    override fun compare(s1: String, s2: String): Int {
        val n1 = s1.length
        val n2 = s2.length
        var i1 = 0
        var i2 = 0

        while (i1 < n1 && i2 < n2) {
            val c1 = s1[i1]
            val c2 = s2[i2]

            if (c1.isDigit() && c2.isDigit()) {
                val num1 = getChunk(s1, n1, i1)
                val num2 = getChunk(s2, n2, i2)
                
                val result = compareNumerically(num1, num2)
                if (result != 0) return result
                
                i1 += num1.length
                i2 += num2.length
            } else {
                val result = c1.lowercaseChar().compareTo(c2.lowercaseChar())
                if (result != 0) return result
                i1++
                i2++
            }
        }
        return n1 - n2
    }

    private fun getChunk(s: String, length: Int, index: Int): String {
        val chunk = StringBuilder()
        var i = index
        while (i < length && s[i].isDigit()) {
            chunk.append(s[i])
            i++
        }
        return chunk.toString()
    }

    private fun compareNumerically(num1: String, num2: String): Int {
        // Hapus leading zeros untuk perbandingan numerik yang akurat
        val s1 = num1.trimStart('0')
        val s2 = num2.trimStart('0')
        
        if (s1.length != s2.length) {
            return s1.length - s2.length
        }
        return s1.compareTo(s2)
    }
}
