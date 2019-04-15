package br.com.tokenlab.edittextmasked

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.*
import java.util.regex.Pattern

/**
 *  This class transforms the inserted string to currency mask
 *
 *  @param editText EditText that will receive the currency mask
 *  @param locale Locale to format the currency with the desired symbol
 */
class CurrencyMaskTextWatcher(private val editText: EditText, private val locale: Locale) : TextWatcher {
    private val emptyValue = getFormattedCurrency(locale, 0.0)
    private val notNumbersPattern = Pattern.compile("[^0-9]")
    private var currentValue = ""

    override fun afterTextChanged(s: Editable?) {
        editText.removeTextChangedListener(this)
        editText.setText(currentValue)
        editText.setSelection(currentValue.length)
        editText.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s.toString() != currentValue) {
            val onlyNumbers = notNumbersPattern.matcher(s).replaceAll("")

            currentValue = if (onlyNumbers.isNotEmpty()) {
                val parsed = onlyNumbers.toDouble()
                getFormattedCurrency(locale, parsed / 100)
            } else emptyValue
        }
    }
}