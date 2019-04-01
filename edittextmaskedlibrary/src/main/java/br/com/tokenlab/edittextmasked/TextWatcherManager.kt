package br.com.tokenlab.edittextmasked

import android.text.SpannableStringBuilder
import android.widget.EditText
import java.util.*

/**
 *  This class aims to avoid the same TextWatcher to be added more than once
 *
 *  @param editText EditText to add masks
 */
class TextWatcherManager(val editText: EditText) {
    private lateinit var currencyMaskTextWatcher: CurrencyMaskTextWatcher
    private lateinit var maskTextWatcher: MaskTextWatcher

    private fun removeMask() {
        if (this::maskTextWatcher.isInitialized)
            editText.removeTextChangedListener(maskTextWatcher)
    }

    fun setMask(mask: String, replaceableSymbol: Char = '#') {
        removeMask()
        maskTextWatcher = MaskTextWatcher(listOf(mask), editText, replaceableSymbol)
        editText.addTextChangedListener(maskTextWatcher)
    }

    fun setMasks(masks: List<String>, replaceableSymbol: Char = '#') {
        removeMask()
        maskTextWatcher = MaskTextWatcher(masks, editText, replaceableSymbol)
        editText.addTextChangedListener(maskTextWatcher)
    }

    private fun removeCurrencyMask() {
        if (this::currencyMaskTextWatcher.isInitialized)
            editText.removeTextChangedListener(currencyMaskTextWatcher)
    }

    fun addCurrencyMask(locale: Locale) {
        removeCurrencyMask()
        currencyMaskTextWatcher = CurrencyMaskTextWatcher(editText, locale)
        with(editText) {
            addTextChangedListener(currencyMaskTextWatcher)
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (text.isEmpty())
                        text = SpannableStringBuilder.valueOf("0")
                    if (text.isNotEmpty())
                        setSelection(text.length)
                }
            }
        }
    }
}