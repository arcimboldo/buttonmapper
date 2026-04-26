package com.github.arcimboldo.buttonmapper

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class ButtonMapperService : AccessibilityService() {

    private lateinit var mappingManager: MappingManager

    companion object {
        var scanListener: ((Int, Int) -> Unit)? = null
        private const val TAG = "ButtonMapperService"
    }

    override fun onCreate() {
        super.onCreate()
        mappingManager = MappingManager(this)
        Log.d(TAG, "Service Created")
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val action = event.action

        // We only care about button down events for triggering actions
        if (action == KeyEvent.ACTION_DOWN) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Key Pressed: $keyCode (scanCode: ${event.scanCode})")
            }

            // Check if we are in scan mode
            scanListener?.let {
                it.invoke(keyCode, event.scanCode)
                // If scanning, we might still want to let the event pass so the user sees something happening,
                // but usually, it's better to consume it so they don't accidentally exit the app while scanning.
                return true 
            }

            // Check for mapping
            val mappedPackage = mappingManager.getMapping(keyCode, event.scanCode)
            if (mappedPackage != null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Mapping found for $keyCode -> $mappedPackage")
                }

                launchApp(mappedPackage)
                return true // Consume event
            }
        }

        return super.onKeyEvent(event)
    }

    private fun launchApp(packageName: String) {
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(launchIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error launching app $packageName", e)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Not used for key remapping but required
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service Interrupted")
    }
}
