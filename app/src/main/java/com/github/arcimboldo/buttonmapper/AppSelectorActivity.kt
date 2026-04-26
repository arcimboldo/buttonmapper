package com.github.arcimboldo.buttonmapper

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AppSelectorActivity : FragmentActivity() {

    data class AppInfo(val name: String, val packageName: String, val icon: Drawable)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_selector)

        val recyclerView = findViewById<RecyclerView>(R.id.app_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val keyCode = intent.getIntExtra("key_code", -1)
        if (keyCode != -1) {
            findViewById<TextView>(R.id.keycode_label).text = "Mapping key code: $keyCode"
        }

        val apps = getInstalledApps()
        recyclerView.adapter = AppAdapter(apps) { app ->
            val resultIntent = Intent()
            resultIntent.putExtra("package_name", app.packageName)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun getInstalledApps(): List<AppInfo> {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        
        // Also check for Leanback launcher (TV apps)
        val tvIntent = Intent(Intent.ACTION_MAIN, null)
        tvIntent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)

        val resolverInfos = pm.queryIntentActivities(intent, 0)
        resolverInfos.addAll(pm.queryIntentActivities(tvIntent, 0))

        return resolverInfos.distinctBy { it.activityInfo.packageName }.map {
            AppInfo(
                it.loadLabel(pm).toString(),
                it.activityInfo.packageName,
                it.loadIcon(pm)
            )
        }.sortedBy { it.name }
    }

    inner class AppAdapter(private val apps: List<AppInfo>, private val onClick: (AppInfo) -> Unit) :
        RecyclerView.Adapter<AppAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val iconView: ImageView = view.findViewById(R.id.app_icon)
            val nameView: TextView = view.findViewById(R.id.app_name)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val app = apps[position]
            holder.nameView.text = app.name
            holder.iconView.setImageDrawable(app.icon)
            holder.itemView.setOnClickListener { onClick(app) }
            holder.itemView.isFocusable = true
            holder.itemView.isFocusableInTouchMode = true
        }

        override fun getItemCount() = apps.size
    }
}
