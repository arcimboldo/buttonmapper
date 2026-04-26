package com.github.arcimboldo.buttonmapper

import android.content.Context
import android.content.SharedPreferences

class MappingManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ButtonMappings", Context.MODE_PRIVATE)

    fun saveMapping(keyCode: Int, packageName: String) {
        prefs.edit().putString(keyCode.toString(), packageName).apply()
    }

    fun getMapping(keyCode: Int): String? {
        // Safety check: Never allow remapping of Back or Home keys
        if (keyCode == 4 || keyCode == 3) {
            return null
        }
        return prefs.getString(keyCode.toString(), null)
    }

    fun removeMapping(keyCode: Int) {
        prefs.edit().remove(keyCode.toString()).apply()
    }

    fun getAllMappings(): Map<Int, String> {
        val all = prefs.all
        val mappings = mutableMapOf<Int, String>()
        for ((key, value) in all) {
            try {
                val keyCode = key.toInt()
                val pkg = value as? String
                if (pkg != null) {
                    mappings[keyCode] = pkg
                }
            } catch (e: Exception) {
                // Ignore non-integer keys
            }
        }
        return mappings
    }
}
