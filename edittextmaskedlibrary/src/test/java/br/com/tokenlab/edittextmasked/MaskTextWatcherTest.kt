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
        val maskTextWatcher = MaskTextWatcher(listOf("##.##-#"), editText)
        editText.addTextChangedListener(maskTextWatcher)

        // WHEN
        editText.text = SpannableStringBuilder("1")

        // THEN
        assertEquals("1", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder("12")

        // THEN
        assertEquals("12.", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder("123")

        // THEN
        assertEquals("12.3", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder("1234")

        // THEN
        assertEquals("12.34-", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder("12345")

        // THEN
        assertEquals("12.34-5", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder("1234589089098")

        // THEN
        assertEquals("12.34-5", editText.text.toString())
    }

    @Test
    fun `is according to the complex provided mask`() {
        // GIVEN
        val maskTextWatcher = MaskTextWatcher(listOf("#$$### ##-#"), editText)
        editText.addTextChangedListener(maskTextWatcher)

        // WHEN
        editText.text = SpannableStringBuilder("1")

        // THEN
        assertEquals("1$$", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder("12")

        // THEN
        assertEquals("1$$2", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder("123")

        // THEN
        assertEquals("1$$23", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder("1234")

        // THEN
        assertEquals("1$$234 ", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder("12345")

        // THEN
        assertEquals("1$$234 5", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder("123456")

        // THEN
        assertEquals("1$$234 56-", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder("1234567")

        // THEN
        assertEquals("1$$234 56-7", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder("1234567856756756")

        // THEN
        assertEquals("1$$234 56-7", editText.text.toString())
    }

    @Test
    fun `erasing text`() {
        // GIVEN
        val maskTextWatcher = MaskTextWatcher(listOf("#- ##+#"), editText)
        editText.addTextChangedListener(maskTextWatcher)
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
    fun `multiple masks`() {
        // GIVEN
        val maskTextWatcher = MaskTextWatcher(listOf("#- ##+#", "#-##/##"), editText)
        editText.addTextChangedListener(maskTextWatcher)

        // WHEN
        editText.text = SpannableStringBuilder("1234")

        // THEN
        assertEquals("1- 23+4", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder(editText.text.toString() + "5")

        // THEN
        assertEquals("1-23/45", editText.text.toString())

        // WHEN
        simulateBackspace()

        // THEN
        assertEquals("1- 23+4", editText.text.toString())
    }

    @Test
    fun `avoiding special characters`() {
        // GIVEN
        val maskTextWatcher = MaskTextWatcher(listOf("#-##/##"), editText)
        editText.addTextChangedListener(maskTextWatcher)

        // WHEN
        editText.text = SpannableStringBuilder("*-()/$%")

        // THEN
        assertEquals("", editText.text.toString())

        // WHEN
        editText.text = SpannableStringBuilder("1-(2/$%")

        // THEN
        assertEquals("1-2", editText.text.toString())
    }

    private fun simulateBackspace() {
        editText.dispatchKeyEvent(
            KeyEvent(
                0, 0, KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_DEL, 0
            )
        )
        editText.dispatchKeyEvent(
            KeyEvent(
                0, 0, KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_DEL, 0
            )
        )
    }
}