package br.com.tokenlab.edittextmasked

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.*
import java.util.regex.Pattern

class CurrencyMaskTextWatcher(private val editText: EditText, private val locale: Locale) : TextWatcher {
    private val emptyValue = getCurrencyFormatted(0.0)
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
            val cleanString = notNumbersPattern.matcher(s).replaceAll("")
            var formatted = emptyValue

            if (cleanString.isNotEmpty()) {
                val parsed = cleanString.toDouble()
                formatted = getCurrencyFormatted(parsed / 100)
            }

            currentValue = formatted
        }
    }

    private fun getCurrencyFormatted(value: Double): String {
        return with(NumberFormat.getCurrencyInstance(locale).format(value)) {
            return@with take(2) + " " + takeLast(length - 2)
        }
    }
}