package br.com.tokenlab.edittextmasked

import android.widget.EditText
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*
import java.util.regex.Pattern

private fun EditText.initTextWatcherManager(): TextWatcherManager {
    // Check if the EditText already have a manager, if not, add as tag
    return getTag(R.id.TAG_TEXT_WATCHER_MANAGER)?.let { it as TextWatcherManager }
        ?: TextWatcherManager(this).apply { setTag(R.id.TAG_TEXT_WATCHER_MANAGER, this) }
}

fun applyMaskToStaticText(string: String, mask: String, replaceableSymbol: Char): String {
    return if (string.isNotEmpty() && mask.isNotEmpty()) {
        var result = mask
        // Replaces the "replaceable symbol" in the sequence they appear in the mask
        string.forEach {
            if (mask.contains(replaceableSymbol)) {
                result = result.replaceFirst(replaceableSymbol, it)
            }
        }
        /*
         * Remove what wasn't replaced
         * Ex: 0#/## -> 0; 08/## -> 08/ etc
         */
        result = result.takeWhile { it != replaceableSymbol }
        result
    } else ""
}

fun String.getRawText(): String {
    val pattern = Pattern.compile("[^a-zA-Z0-9]")
    return pattern.matcher(this).replaceAll("")
}

fun String.setMask(mask: String, replaceableSymbol: Char = '#'): String {
    return applyMaskToStaticText(this.getRawText(), mask, replaceableSymbol)
}

fun EditText.setMask(mask: String, replaceableSymbol: Char = '#') {
    initTextWatcherManager().setMask(mask, replaceableSymbol)
}

fun EditText.setMasks(masks: List<String>, replaceableSymbol: Char = '#') {
    initTextWatcherManager().setMasks(masks, replaceableSymbol)
}

fun getFormattedCurrency(locale: Locale, value: Any): String {
    // Get the currency string and add space if it doesn't contains
    return try {
        with(NumberFormat.getCurrencyInstance(locale).format(value)) {
            if (!contains(" ")) {
                val indexOfFirstDigit = indexOfFirst { it.isDigit() }
                take(indexOfFirstDigit) + " " + substring(indexOfFirstDigit)
            } else this
        }
    } catch (e: IllegalArgumentException) {
        ""
    }
}

fun EditText.addCurrencyMask(locale: Locale) {
    initTextWatcherManager().addCurrencyMask(locale)
}

fun String.currencyToBigDecimal(): BigDecimal? {
    return try {
        val value = replace("[^0-9,]".toRegex(), "").replace(",", ".").toBigDecimal()
        if (value < BigDecimal.valueOf(0.01))
            BigDecimal.ZERO
        else value
    } catch (e: NumberFormatException) {
        null
    }
}

fun String.currencyToDouble(): Double? {
    return try {
        replace("[^0-9,]".toRegex(), "").replace(",", ".").toDouble()
    } catch (e: NumberFormatException) {
        null
    }
}

fun BigDecimal.formatAsCurrency(locale: Locale): String {
    return getFormattedCurrency(locale, this)
}

fun Double.formatAsCurrency(locale: Locale): String {
    return getFormattedCurrency(locale, this)
}