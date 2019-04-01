package br.com.tokenlab.edittextmasked

import android.view.View
import android.widget.EditText
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*
import java.util.regex.Pattern

private val textWatcherManagers: MutableList<TextWatcherManager> = mutableListOf()

private fun EditText.initTextWatcherManager(): TextWatcherManager {
    val textWatcherManager = textWatcherManagers.firstOrNull { it.editText.id == this.id }
        ?: TextWatcherManager(this).apply { textWatcherManagers.add(this) }

    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View?) {
            textWatcherManager.removeAllMaskTextWatcher()
            textWatcherManagers.remove(textWatcherManager)
        }

        override fun onViewAttachedToWindow(v: View?) {}
    })

    return textWatcherManager
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

fun EditText.addCurrencyMask(locale: Locale) {
    initTextWatcherManager().addCurrencyMask(locale)
}

fun String.removeCurrencyMask(): BigDecimal? {
    return try {
        val value = replace("[^0-9,]".toRegex(), "").replace(",", ".").toBigDecimal()
        if (value < BigDecimal.valueOf(0.01))
            BigDecimal.ZERO
        else value
    } catch (e: Exception) {
        null
    }
}