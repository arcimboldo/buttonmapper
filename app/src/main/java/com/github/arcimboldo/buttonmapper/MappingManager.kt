package com.github.arcimboldo.buttonmapper

import android.content.Context
import android.content.SharedPreferences

class MappingManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ButtonMappings", Context.MODE_PRIVATE)

    fun saveMapping(keyCode: Int, scanCode: Int, packageName: String) {
        prefs.edit().putString("$keyCode:$scanCode", packageName).apply()
    }

    fun getMapping(keyCode: Int, scanCode: Int): String? {
        // Safety check: Never allow remapping of Back or Home keys
        if (keyCode == 4 || keyCode == 3) {
            return null
        }
        return prefs.getString("$keyCode:$scanCode", null)
    }

    fun removeMapping(keyCode: Int, scanCode: Int) {
        prefs.edit().remove("$keyCode:$scanCode").apply()
    }

    fun getAllMappings(): Map<String, String> {
        val all = prefs.all
        val mappings = mutableMapOf<String, String>()
        for ((key, value) in all) {
            val pkg = value as? String
            if (pkg != null) {
                mappings[key] = pkg
            }
        }
        return mappings
    }
}
