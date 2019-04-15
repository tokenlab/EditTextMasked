package br.com.tokenlab.edittextmasked

import android.text.SpannableStringBuilder
import android.view.KeyEvent
import android.widget.EditText
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.math.BigDecimal
import java.util.*

/**
 *  This test will fail if run all at the same time, but will work if run individually and I don't know why.
 *  If you discover why, please let me know
 */
@RunWith(RobolectricTestRunner::class)
class CurrencyMaskTextWatcherTest {

    @Test
    fun `formatting R$ properly`() {
        with(EditText(RuntimeEnvironment.systemContext)) {
            // GIVEN
            addCurrencyMask(Locale("pt", "BR"))

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_2)

            // THEN
            assertEquals("R$ 0,02", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_3)

            // THEN
            assertEquals("R$ 0,23", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_4)

            // THEN
            assertEquals("R$ 2,34", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_5)

            // THEN
            assertEquals("R$ 23,45", text.toString())
        }
    }

    @Test
    fun `formatting $ properly`() {
        with(EditText(RuntimeEnvironment.systemContext)) {
            // GIVEN
            addCurrencyMask(Locale("en", "US"))

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_2)

            // THEN
            assertEquals("$ 0.02", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_3)

            // THEN
            assertEquals("$ 0.23", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_4)

            // THEN
            assertEquals("$ 2.34", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_5)

            // THEN
            assertEquals("$ 23.45", text.toString())
        }
    }

    @Test
    fun `erasing text`() {
        with(EditText(RuntimeEnvironment.systemContext)) {
            // GIVEN
            addCurrencyMask(Locale("pt", "BR"))
            text = SpannableStringBuilder("R$ 23,45")

            // WHEN
            simulateBackspace()

            // THEN
            Assert.assertEquals("R$ 2,34", text.toString())

            // WHEN
            simulateBackspace()

            // THEN
            Assert.assertEquals("R$ 0,23", text.toString())

            // WHEN
            simulateBackspace()

            // THEN
            Assert.assertEquals("R$ 0,02", text.toString())

            // WHEN
            simulateBackspace()

            // THEN
            Assert.assertEquals("R$ 0,00", text.toString())

            // WHEN
            simulateBackspace()

            // THEN
            Assert.assertEquals("R$ 0,00", text.toString())
        }
    }

    @Test
    fun `avoiding special characters`() {
        with(EditText(RuntimeEnvironment.systemContext)) {
            // GIVEN
            addCurrencyMask(Locale("en", "US"))

            // WHEN
            simulateKeyPress(
                listOf(
                    KeyEvent.KEYCODE_EQUALS,
                    KeyEvent.KEYCODE_LEFT_BRACKET,
                    KeyEvent.KEYCODE_RIGHT_BRACKET,
                    KeyEvent.KEYCODE_SEMICOLON
                )
            )

            // THEN
            Assert.assertEquals("$ 0.00", text.toString())

            // WHEN
            simulateKeyPress(
                listOf(
                    KeyEvent.KEYCODE_EQUALS,
                    KeyEvent.KEYCODE_1,
                    KeyEvent.KEYCODE_LEFT_BRACKET,
                    KeyEvent.KEYCODE_RIGHT_BRACKET,
                    KeyEvent.KEYCODE_SEMICOLON,
                    KeyEvent.KEYCODE_2
                )
            )

            // THEN
            Assert.assertEquals("$ 0.12", text.toString())
        }
    }

    @Test
    fun `removing currency mask R$ to BigDecimal`() {
        // GIVEN
        val currency = "R$ 87,94"

        // WHEN
        val bigDecimal = currency.currencyToBigDecimal()

        // THEN
        assertEquals(BigDecimal.valueOf(87.94), bigDecimal)
    }

    @Test
    fun `removing currency mask $ to BigDecimal`() {
        // GIVEN
        val currency = "$ 07,94"

        // WHEN
        val bigDecimal = currency.currencyToBigDecimal()

        // THEN
        assertEquals(BigDecimal.valueOf(7.94), bigDecimal)
    }

    @Test
    fun `removing currency mask R$ to Double`() {
        // GIVEN
        val currency = "R$ 3,89"

        // WHEN
        val double = currency.currencyToDouble()

        // THEN
        assertEquals(3.89, double)
    }

    @Test
    fun `removing currency mask $ to Double`() {
        // GIVEN
        val currency = "$ 456,78"

        // WHEN
        val double = currency.currencyToDouble()

        // THEN
        assertEquals(456.78, double)
    }

    @Test
    fun `formatting BigDecimal as currency string`() {
        // GIVEN
        val bigDecimal = BigDecimal.valueOf(8.36)

        // WHEN
        val string = bigDecimal.formatAsCurrency(Locale("pt", "BR"))

        // THEN
        assertEquals("R$ 8,36", string)
    }

    @Test
    fun `formatting Double as currency string`() {
        // GIVEN
        val double = 789.36

        // WHEN
        val string = double.formatAsCurrency(Locale("en", "US"))

        // THEN
        assertEquals("$ 789.36", string)
    }
}