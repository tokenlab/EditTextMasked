package br.com.tokenlab.edittextmasked

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.regex.Pattern

/**
 * This class was made to format the inserted texts in the configured mask.
 * Accept any type of mask, except currency mask (@see CurrencyMaskTextWatcher).
 *
 * Receive an array of masks:
 * @param masks ##/##; ##/##/####; #### #### #### #### etc.
 * @param editText EditText that will receive the mask
 * @param replaceableSymbol mask symbol that will be replaced
 */
class MaskTextWatcher(
    masks: List<String>,
    private val editText: EditText,
    private val replaceableSymbol: Char = '#'
) : TextWatcher {

    private val masks: List<String>? = if (masks.isNotEmpty()) masks else null
    private val cleanMaskPattern = Pattern.compile("[^$replaceableSymbol]")

    private var lastDigitErasedWasSymbol: Boolean = false
    private var lastVersion: String = ""
    private var currentMask = ""
    private var lastSymbolErased: String = ""
    private var maxLength: Int = 0
        get() {
            return if (field == 0)
                masks?.map { it.length }?.max() ?: 0
            else
                field
        }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val string = s.toString()

        if (string != lastVersion) {
            // Check whether the text was pre-populated OR the cursor is at the end
            if ((start == 0 && string.length > 1) || (editText.selectionStart < string.length)) {
                applyMaskForPreFilledText(string)
            } else {
                // Check if it isn't backspace
                if (string.length != start) {
                    // Avoid special characters
                    if (string.isNotEmpty() && !string.last().isLetterOrDigit()) {
                        lastVersion = string.dropLast(1)
                        return
                    }

                    // If the last character erased has been a symbol, it restructures the string
                    if (lastDigitErasedWasSymbol) {
                        applyMaskAfterSymbolErased(string)
                    } else {
                        applyMaskInNormalSituation(string)
                    }
                } else {
                    // Backspace
                    applyMaskOnBackspace(string)
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable) {
        editText.removeTextChangedListener(this)
        editText.setText(lastVersion)
        repositionCursor()
        editText.addTextChangedListener(this)
    }

    private fun applyMaskForPreFilledText(originalString: String) {
        val cleanString = originalString.getRawText()

        // Apply the mask that fits
        masks?.firstOrNull { cleanString.length <= cleanMaskPattern.matcher(it).replaceAll("").length }
            ?.apply {
                currentMask = this
                lastVersion = applyMaskToStaticText(cleanString, this, replaceableSymbol)
            }
    }

    private fun applyMaskOnBackspace(originalString: String) {
        // Check if the last character erased was a symbol
        lastVersion = if (lastVersion.isNotEmpty() && !lastVersion.last().isLetterOrDigit()) {
            removeDigitBeforeSymbol(originalString)
        } else {
            removeSymbolBeforeDigit(originalString)
        }

        val cleanString = lastVersion.getRawText()
        // Return the previous mask, if any
        masks?.firstOrNull { cleanString.length == cleanMaskPattern.matcher(it).replaceAll("").length }
            ?.apply {
                currentMask = this
                lastVersion = applyMaskToStaticText(cleanString, currentMask, replaceableSymbol)
            }
    }

    private fun applyMaskAfterSymbolErased(originalString: String) {
        lastVersion = originalString.substring(0, originalString.lastIndex) +
                lastSymbolErased + originalString[originalString.lastIndex]

        lastDigitErasedWasSymbol = false
    }

    private fun applyMaskInNormalSituation(originalString: String) {
        masks?.let {
            // Check if the string isn't bigger than all masks
            if (originalString.length <= maxLength) {
                // Find the better mask
                val betterMask = it.firstOrNull { mask -> originalString.length <= mask.length }
                    ?: it[0]

                lastVersion = if (betterMask != currentMask) {
                    currentMask = betterMask
                    applyMaskToStaticText(originalString.getRawText(), currentMask, replaceableSymbol)
                } else {
                    applyDynamicMask(originalString, currentMask)
                }
            }
        }
    }

    private fun applyDynamicMask(charSequence: CharSequence, mask: String): String {
        var string = charSequence.toString()
        if (string.isNotEmpty() && mask.isNotEmpty()) {
            if (string.length == 1 && mask.first() != replaceableSymbol)
                return mask.first() + string

            val maskIndex = string.length

            // Places the mask symbol
            if (mask.length > maskIndex) {
                val maskToApply = mask.substring(maskIndex)
                maskToApply.takeWhile { it != replaceableSymbol }
                    .apply { string += (this + "") }
            }

        }
        return string
    }

    private fun repositionCursor() {
        // Put the cursor at the end
        editText.setSelection(editText.text.toString().length)
    }

    private fun removeSymbolBeforeDigit(string: String): String {
        var result = string
        if (string.length > 1) {
            val lastChar = string.takeLastWhile { !it.isLetterOrDigit() }
            lastDigitErasedWasSymbol = lastChar.isNotEmpty()

            // Check if the last character is a symbol
            if (lastDigitErasedWasSymbol) {
                lastSymbolErased = lastChar
                result = string.substring(0, string.length - lastChar.length)
            }
        } else {
            lastDigitErasedWasSymbol = false
        }
        return result
    }

    private fun removeDigitBeforeSymbol(string: String): String {
        val result = if (string.isNotEmpty()) string.substring(0, string.indexOfLast { it.isLetterOrDigit() }) else ""
        lastDigitErasedWasSymbol = false
        return result
    }
}
