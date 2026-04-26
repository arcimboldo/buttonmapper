package com.github.arcimboldo.buttonmapper

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import android.view.KeyEvent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : FragmentActivity() {

    private lateinit var mappingManager: MappingManager
    private lateinit var adapter: MappingAdapter
    private var pendingKeyCode: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!isAccessibilityServiceEnabled()) {
            showAccessibilityPrompt()
        }

        mappingManager = MappingManager(this)

        val recyclerView = findViewById<RecyclerView>(R.id.mapping_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MappingAdapter(mappingManager.getAllMappings().toList()) { keyCode ->
            mappingManager.removeMapping(keyCode)
            refreshList()
        }
        recyclerView.adapter = adapter
        refreshList() // Check for empty state on start

        findViewById<Button>(R.id.btn_add_mapping).setOnClickListener {
            startScanning()
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedComponentName = android.content.ComponentName(this, ButtonMapperService::class.java)
        val enabledServices = android.provider.Settings.Secure.getString(
            contentResolver,
            android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.contains(expectedComponentName.flattenToString())
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun showAccessibilityPrompt() {
        AlertDialog.Builder(this)
            .setTitle("Accessibility Service Required")
            .setMessage("This app needs the Accessibility Service to be enabled to intercept button presses. Please enable it in Settings > System > Accessibility.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("Later", null)
            .show()
    }

    private fun refreshList() {
        val mappings = mappingManager.getAllMappings().toList()
        adapter.updateMappings(mappings)
        findViewById<View>(R.id.empty_text).visibility = if (mappings.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun startScanning() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.add_mapping)
            .setMessage(R.string.scan_button_instruction)
            .setNegativeButton("Cancel") { _, _ -> ButtonMapperService.scanListener = null }
            .create()

        ButtonMapperService.scanListener = { keyCode ->
            if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
                // Ignore system keys to prevent lockout

            } else {
                runOnUiThread {
                    pendingKeyCode = keyCode
                    ButtonMapperService.scanListener = null
                    dialog.dismiss()
                    val intent = Intent(this, AppSelectorActivity::class.java)
                    startActivityForResult(intent, 100)
                }
            }
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val packageName = data?.getStringExtra("package_name")
            val keyCode = pendingKeyCode
            if (packageName != null && keyCode != null) {
                mappingManager.saveMapping(keyCode, packageName)
                Toast.makeText(this, R.string.mapping_saved, Toast.LENGTH_SHORT).show()
                refreshList()
            }
        }
    }

    inner class MappingAdapter(
        private var mappings: List<Pair<Int, String>>,
        private val onDelete: (Int) -> Unit
    ) : RecyclerView.Adapter<MappingAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val keyLabel: TextView = view.findViewById(R.id.key_label)
            val appLabel: TextView = view.findViewById(R.id.app_label)
            val btnDelete: Button = view.findViewById(R.id.btn_delete)
        }

        fun updateMappings(newMappings: List<Pair<Int, String>>) {
            mappings = newMappings
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mapping, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val (keyCode, pkg) = mappings[position]
            holder.keyLabel.text = "Key Code: $keyCode"
            
            val appLabel = try {
                val info = packageManager.getApplicationInfo(pkg, 0)
                packageManager.getApplicationLabel(info).toString()
            } catch (e: Exception) {
                pkg
            }
            holder.appLabel.text = appLabel
            
            holder.btnDelete.setOnClickListener { onDelete(keyCode) }
            holder.itemView.isFocusable = true
        }

        override fun getItemCount() = mappings.size
    }
}
