package br.com.tokenlab.edittextmasked

import android.view.KeyEvent
import android.widget.EditText

fun EditText.simulateKeyPress(keys: List<Int> = emptyList()) {
    keys.forEach { simulateKeyPress(it) }
}

fun EditText.simulateKeyPress(key: Int = 0) {
    dispatchKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, key, 0))
    dispatchKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_UP, key, 0))
}

fun EditText.simulateBackspace() {
    dispatchKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0))
    dispatchKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0))
}