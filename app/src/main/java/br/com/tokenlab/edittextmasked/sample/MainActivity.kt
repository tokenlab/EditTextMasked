package br.com.tokenlab.edittextmasked.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableStringBuilder
import br.com.tokenlab.edittextmasked.R
import br.com.tokenlab.edittextmasked.setMask
import br.com.tokenlab.edittextmasked.setMasks
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextPhone.setMask(mask = "(**) *****-****", replaceableSymbol = '*')
        editTextPreFilledPhone.setMask("(##) #####-####")
        editTextDate.setMask("##/##/####")
        editTextDocument.setMask("###.###.###-##")
        editTextMultipleDocuments.setMasks(listOf("###.###.###-##", "##.###.###/####-##"))

        editTextPreFilledPhone.text = SpannableStringBuilder.valueOf("99123456789")
        val someDate = "13032019"
        textViewDate.text = someDate.setMask("##/##/####")
    }
}