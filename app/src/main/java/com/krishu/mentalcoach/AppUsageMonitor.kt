package com.krishu.mentalcoach

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process

class AppUsageMonitor(private val context: Context) {

    fun hasUsageAccess(): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }

        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun getRecentExternalPackageNames(): List<String> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val endTime = System.currentTimeMillis()
        val startTime = endTime - 10 * 60 * 1000

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val event = UsageEvents.Event()

        val packageNames = mutableListOf<String>()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            val isForegroundEvent =
                event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                        event.eventType == UsageEvents.Event.ACTIVITY_RESUMED

            val isNotOurApp = event.packageName != context.packageName

            if (isForegroundEvent && isNotOurApp) {
                packageNames.add(event.packageName)
            }
        }

        return packageNames.distinct()
    }

    fun getLastExternalUsedPackageName(): String? {
        return getRecentExternalPackageNames().lastOrNull()
    }
}