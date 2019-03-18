package br.com.tokenlab.edittextmasked

import android.text.SpannableStringBuilder
import android.view.KeyEvent
import android.widget.EditText
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class MaskTextWatcherTest {
    lateinit var editText: EditText

    @Before
    fun setup() {
        editText = EditText(RuntimeEnvironment.systemContext)
    }

    @Test
    fun `is according to the simple provided mask`() {
        // GIVEN
        editText.setMask("##.##-#")

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_1)

        // THEN
        assertEquals("1", editText.text.toString())

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_2)

        // THEN
        assertEquals("12.", editText.text.toString())

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_3)

        // THEN
        assertEquals("12.3", editText.text.toString())

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_4)

        // THEN
        assertEquals("12.34-", editText.text.toString())

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_5)

        // THEN
        assertEquals("12.34-5", editText.text.toString())

        // WHEN
        simulateKeyPress(listOf(KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_8))

        // THEN
        assertEquals("12.34-5", editText.text.toString())
    }

    @Test
    fun `is according to the complex provided mask`() {
        // GIVEN
        editText.setMask("#$$### ##-#")

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_1)

        // THEN
        assertEquals("1$$", editText.text.toString())

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_2)

        // THEN
        assertEquals("1$$2", editText.text.toString())

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_3)

        // THEN
        assertEquals("1$$23", editText.text.toString())

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_4)

        // THEN
        assertEquals("1$$234 ", editText.text.toString())

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_5)

        // THEN
        assertEquals("1$$234 5", editText.text.toString())

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_6)

        // THEN
        assertEquals("1$$234 56-", editText.text.toString())

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_7)

        // THEN
        assertEquals("1$$234 56-7", editText.text.toString())

        // WHEN
        simulateKeyPress(listOf(KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_1))

        // THEN
        assertEquals("1$$234 56-7", editText.text.toString())
    }

    @Test
    fun `erasing text`() {
        // GIVEN
        editText.setMask("#- ##+#")
        editText.text = SpannableStringBuilder("1- 23+4")

        // WHEN
        simulateBackspace()

        // THEN
        assertEquals("1- 23", editText.text.toString())

        // WHEN
        simulateBackspace()

        // THEN
        assertEquals("1- 2", editText.text.toString())

        // WHEN
        simulateBackspace()

        // THEN
        assertEquals("1", editText.text.toString())
    }

    @Test
    fun `removing digit before mask symbol`() {
        // GIVEN
        editText.setMask("#- ##+#")

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_1)

        // THEN
        assertEquals("1- ", editText.text.toString())

        // WHEN
        simulateBackspace()

        // THEN
        assertEquals("", editText.text.toString())
    }

    @Test
    fun `inputting after to erase text`() {
        // GIVEN
        editText.setMask("#- ##+#")
        editText.text = SpannableStringBuilder("1- 23+4")

        // WHEN
        simulateBackspace()

        // THEN
        assertEquals("1- 23", editText.text.toString())

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_4)

        // THEN
        assertEquals("1- 23+4", editText.text.toString())
    }

    @Test
    fun `multiple masks`() {
        // GIVEN
        editText.setMasks(listOf("#- ##+#", "#-##/##+#"))

        // WHEN
        simulateKeyPress(listOf(KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_4))

        // THEN
        assertEquals("1- 23+4", editText.text.toString())

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_5)

        // THEN
        assertEquals("1-23/45+", editText.text.toString())

        // WHEN
        simulateKeyPress(KeyEvent.KEYCODE_6)

        // THEN
        assertEquals("1-23/45+6", editText.text.toString())

        // WHEN
        simulateBackspace()
        simulateBackspace()

        // THEN
        assertEquals("1- 23+4", editText.text.toString())
    }

    @Test
    fun `avoiding special characters`() {
        // GIVEN
        editText.setMask("#-##/##")

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
        assertEquals("", editText.text.toString())

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
        assertEquals("1-2", editText.text.toString())
    }

    @Test
    fun `avoiding special characters pre-filled`() {
        // GIVEN
        editText.setMask("#-##/##")

        // WHEN
        editText.text = SpannableStringBuilder("*-()/$%")

        // THEN
        assertEquals("", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder("1-(2/$%")

        // THEN
        assertEquals("1-2", editText.text.toString())
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

    private fun simulateKeyPress(keys: List<Int> = emptyList()) {
        keys.forEach { simulateKeyPress(it) }
    }

    private fun simulateKeyPress(key: Int = 0) {
        editText.dispatchKeyEvent(
            KeyEvent(0, 0, KeyEvent.ACTION_DOWN, key, 0)
        )
        editText.dispatchKeyEvent(
            KeyEvent(0, 0, KeyEvent.ACTION_UP, key, 0)
        )
    }

    private fun simulateBackspace() {
        editText.dispatchKeyEvent(
            KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0)
        )
        editText.dispatchKeyEvent(
            KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0)
        )
    }
}