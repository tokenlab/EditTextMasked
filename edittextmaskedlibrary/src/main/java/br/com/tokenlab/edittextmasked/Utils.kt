package br.com.tokenlab.edittextmasked

import android.widget.EditText
import java.util.regex.Pattern

fun String.getRawText(): String {
    val pattern = Pattern.compile("[^a-zA-Z0-9]")
    return pattern.matcher(this).replaceAll("")
}

fun String.setMask(mask: String, replaceableSymbol: Char = '#'): String {
    return applyMaskToStaticText(this.getRawText(), mask, replaceableSymbol)
}

fun EditText.setMasks(masks: List<String>, replaceableSymbol: Char = '#') {
    addTextChangedListener(MaskTextWatcher(masks, this, replaceableSymbol))
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