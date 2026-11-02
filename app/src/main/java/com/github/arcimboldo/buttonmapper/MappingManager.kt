package com.github.arcimboldo.buttonmapper

import android.content.Context
import android.content.SharedPreferences
import android.view.KeyEvent

val PROTECTED_KEYS = setOf(
    KeyEvent.KEYCODE_BACK,
    KeyEvent.KEYCODE_HOME,
    KeyEvent.KEYCODE_DPAD_UP,
    KeyEvent.KEYCODE_DPAD_DOWN,
    KeyEvent.KEYCODE_DPAD_LEFT,
    KeyEvent.KEYCODE_DPAD_RIGHT,
    KeyEvent.KEYCODE_DPAD_CENTER,
    KeyEvent.KEYCODE_ENTER,
    KeyEvent.KEYCODE_NUMPAD_ENTER,
)

val ENTER_KEYS = setOf(
    KeyEvent.KEYCODE_DPAD_CENTER,
    KeyEvent.KEYCODE_ENTER,
    KeyEvent.KEYCODE_NUMPAD_ENTER,
)

fun isProtectedKey(keyCode: Int): Boolean = keyCode in PROTECTED_KEYS
fun isEnterKey(keyCode: Int): Boolean = keyCode in ENTER_KEYS

class MappingManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ButtonMappings", Context.MODE_PRIVATE)

    fun saveMapping(keyCode: Int, scanCode: Int, packageName: String) {
        if (isProtectedKey(keyCode)) {
            return
        }
        prefs.edit().putString("$keyCode:$scanCode", packageName).apply()
    }

    fun getMapping(keyCode: Int, scanCode: Int): String? {
        if (isProtectedKey(keyCode)) {
            return null
        }
        return prefs.getString("$keyCode:$scanCode", null)
    }

    fun removeMapping(keyCode: Int, scanCode: Int) {
        prefs.edit().remove("$keyCode:$scanCode").apply()
    }

    fun removeMapping(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun getAllMappings(): Map<String, String> {
        val all = prefs.all
        val mappings = mutableMapOf<String, String>()
        for ((key, value) in all) {
            val pkg = value as? String ?: continue
            val keyCode = key.substringBefore(':').toIntOrNull()
            if (keyCode == null || !isProtectedKey(keyCode)) {
                mappings[key] = pkg
            }
        }
        return mappings
    }
}
