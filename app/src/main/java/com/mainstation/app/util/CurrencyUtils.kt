package com.mainstation.app.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    fun toRupiah(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0 // Remove decimals for cleaner look
        return format.format(amount)
    }
}
