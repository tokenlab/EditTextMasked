package br.com.tokenlab.edittextmasked.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.SpannableStringBuilder
import br.com.tokenlab.edittextmasked.*
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextPhone.setMask(mask = "(**) *****-****", replaceableSymbol = '*')
        editTextPreFilledPhone.setMask("(##) #####-####")
        editTextDate.setMask("##/##/####")
        editTextDocument.setMask("###.###.###-##")
        editTextMultipleDocuments.setMasks(listOf("###.###.###-##", "##.###.###/####-##"))
        editTextCurrency.addCurrencyMask(Locale("pt", "BR"))

        editTextPreFilledPhone.text = SpannableStringBuilder.valueOf("99123456789")

        textViewBigDecimal.text = BigDecimal.valueOf(234.34).formatAsCurrency(Locale("en", "US"))
        textViewDouble.text = 234.34.formatAsCurrency(Locale("pt", "BR"))

        val someDate = "13032019"
        textViewDate.text = someDate.setMask("##/##/####")
    }
}