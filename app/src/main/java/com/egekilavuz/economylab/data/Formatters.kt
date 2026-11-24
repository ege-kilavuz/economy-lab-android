package com.egekilavuz.economylab.data

import java.text.NumberFormat

/**
 * Eski oyun motorundan taşınan ortak biçimlendirme yardımcısı.
 * Simülasyonlar ve mini oyunlar tarafından kullanılır.
 */
fun Int.toLocaleString(): String =
    NumberFormat.getIntegerInstance().format(this)
