package br.com.tokenlab.edittextmasked

import android.text.SpannableStringBuilder
import android.view.KeyEvent
import android.widget.EditText
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 *  This test will fail if run all at the same time, but will work if run individually and I don't know why.
 *  If you discover why, please let me know
 */
@RunWith(RobolectricTestRunner::class)
class MaskTextWatcherTest {

    @Test
    fun `is according to the simple provided mask`() {
        with(EditText(RuntimeEnvironment.systemContext)) {
            // GIVEN
            setMask("##.##-#")

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_1)

            // THEN
            assertEquals("1", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_2)

            // THEN
            assertEquals("12.", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_3)

            // THEN
            assertEquals("12.3", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_4)

            // THEN
            assertEquals("12.34-", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_5)

            // THEN
            assertEquals("12.34-5", text.toString())

            // WHEN
            simulateKeyPress(listOf(KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_8))

            // THEN
            assertEquals("12.34-5", text.toString())
        }
    }

    @Test
    fun `is according to the complex provided mask`() {
        with(EditText(RuntimeEnvironment.systemContext)) {
            // GIVEN
            setMask("#$$### ##-#")

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_1)

            // THEN
            assertEquals("1$$", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_2)

            // THEN
            assertEquals("1$$2", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_3)

            // THEN
            assertEquals("1$$23", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_4)

            // THEN
            assertEquals("1$$234 ", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_5)

            // THEN
            assertEquals("1$$234 5", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_6)

            // THEN
            assertEquals("1$$234 56-", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_7)

            // THEN
            assertEquals("1$$234 56-7", text.toString())

            // WHEN
            simulateKeyPress(listOf(KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_1))

            // THEN
            assertEquals("1$$234 56-7", text.toString())
        }
    }

    @Test
    fun `erasing text`() {
        with(EditText(RuntimeEnvironment.systemContext)) {
            // GIVEN
            setMask("#- ##+#")
            text = SpannableStringBuilder("1- 23+4")

            // WHEN
            simulateBackspace()

            // THEN
            assertEquals("1- 23", text.toString())

            // WHEN
            simulateBackspace()

            // THEN
            assertEquals("1- 2", text.toString())

            // WHEN
            simulateBackspace()

            // THEN
            assertEquals("1", text.toString())
        }
    }

    @Test
    fun `removing digit before mask symbol`() {
        with(EditText(RuntimeEnvironment.systemContext)) {
            // GIVEN
            setMask("#- ##+#")

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_1)

            // THEN
            assertEquals("1- ", text.toString())

            // WHEN
            simulateBackspace()

            // THEN
            assertEquals("", text.toString())
        }
    }

    @Test
    fun `inputting after to erase text`() {
        with(EditText(RuntimeEnvironment.systemContext)) {
            // GIVEN
            setMask("#- ##+#")
            text = SpannableStringBuilder("1- 23+4")

            // WHEN
            simulateBackspace()

            // THEN
            assertEquals("1- 23", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_4)

            // THEN
            assertEquals("1- 23+4", text.toString())
        }
    }

    @Test
    fun `multiple masks`() {
        with(EditText(RuntimeEnvironment.systemContext)) {
            // GIVEN
            setMasks(listOf("#- ##+#", "#-##/##+#"))

            // WHEN
            simulateKeyPress(
                listOf(
                    KeyEvent.KEYCODE_1,
                    KeyEvent.KEYCODE_2,
                    KeyEvent.KEYCODE_3,
                    KeyEvent.KEYCODE_4
                )
            )

            // THEN
            assertEquals("1- 23+4", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_5)

            // THEN
            assertEquals("1-23/45+", text.toString())

            // WHEN
            simulateKeyPress(KeyEvent.KEYCODE_6)

            // THEN
            assertEquals("1-23/45+6", text.toString())

            // WHEN
            simulateBackspace()
            simulateBackspace()

            // THEN
            assertEquals("1- 23+4", text.toString())
        }
    }

    @Test
    fun `avoiding special characters`() {
        with(EditText(RuntimeEnvironment.systemContext)) {
            // GIVEN
            setMask("#-##/##")

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
            assertEquals("", text.toString())

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
            assertEquals("1-2", text.toString())
        }
    }

    @Test
    fun `avoiding special characters pre-filled`() {
        with(EditText(RuntimeEnvironment.systemContext)) {
            // GIVEN
            setMask("#-##/##")

            // WHEN
            text = SpannableStringBuilder("*-()/$%")

            // THEN
            assertEquals("", text.toString())

            // WHEN
            text = SpannableStringBuilder("1-(2/$%")

            // THEN
            assertEquals("1-2", text.toString())
        }
    }

    @Test
    fun `string with mask`() {
        // GIVEN
        val string = "18032019"

        // WHEN
        val maskedString = string.setMask("##/##/####")

        // THEN
        assertEquals("18/03/2019", maskedString)
    }

    @Test
    fun `string without mask`() {
        // GIVEN
        val string = "18032019"
        val maskedString = string.setMask("##/##/####")

        // WHEN
        val unmaskedString = maskedString.getRawText()

        // THEN
        assertEquals(string, unmaskedString)
    }
}