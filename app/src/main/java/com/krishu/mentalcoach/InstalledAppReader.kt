package com.krishu.mentalcoach

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

class InstalledAppReader(private val context: Context) {

    fun getInstalledApps(): List<InstalledAppInfo> {
        val packageManager = context.packageManager

        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        return apps
            .filter { appInfo ->
                packageManager.getLaunchIntentForPackage(appInfo.packageName) != null
            }
            .map { appInfo ->
                InstalledAppInfo(
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    packageName = appInfo.packageName
                )
            }
            .sortedBy { app ->
                app.appName.lowercase()
            }
    }
}