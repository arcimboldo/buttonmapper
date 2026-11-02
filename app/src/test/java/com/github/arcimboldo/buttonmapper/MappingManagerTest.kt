package com.github.arcimboldo.buttonmapper

import android.view.KeyEvent
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MappingManagerTest {
    @Test
    fun enterKeysAreRecognizedAsScanCancelKeys() {
        assertTrue(isEnterKey(KeyEvent.KEYCODE_DPAD_CENTER))
        assertTrue(isEnterKey(KeyEvent.KEYCODE_ENTER))
        assertTrue(isEnterKey(KeyEvent.KEYCODE_NUMPAD_ENTER))
        assertFalse(isEnterKey(KeyEvent.KEYCODE_BACK))
        assertFalse(isEnterKey(KeyEvent.KEYCODE_HOME))
        assertFalse(isEnterKey(KeyEvent.KEYCODE_DPAD_UP))
    }

    @Test
    fun navigationAndEnterKeysAreProtectedFromMapping() {
        assertTrue(isProtectedKey(KeyEvent.KEYCODE_BACK))
        assertTrue(isProtectedKey(KeyEvent.KEYCODE_HOME))
        assertTrue(isProtectedKey(KeyEvent.KEYCODE_DPAD_UP))
        assertTrue(isProtectedKey(KeyEvent.KEYCODE_DPAD_CENTER))
        assertTrue(isProtectedKey(KeyEvent.KEYCODE_ENTER))
        assertTrue(isProtectedKey(KeyEvent.KEYCODE_NUMPAD_ENTER))
        assertFalse(isProtectedKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE))
    }
}
