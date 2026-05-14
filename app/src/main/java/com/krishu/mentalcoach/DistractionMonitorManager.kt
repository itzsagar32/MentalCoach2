package com.krishu.mentalcoach

import android.os.Handler
import android.os.Looper

class DistractionMonitorManager(
    private val appUsageMonitor: AppUsageMonitor
) {
    private val monitoringHandler = Handler(Looper.getMainLooper())
    private var isMonitoring = false
    private var lastWarnedPackage: String? = null

    fun startMonitoring(
        distractionApps: List<String>,
        onDistractionDetected: (String) -> Unit
    ) {
        stopMonitoring()

        isMonitoring = true
        lastWarnedPackage = null

        val monitoringRunnable = object : Runnable {
            override fun run() {
                if (!isMonitoring) {
                    return
                }

                val packageName = appUsageMonitor.getLastExternalUsedPackageName()

                if (packageName != null && distractionApps.contains(packageName)) {
                    if (lastWarnedPackage != packageName) {
                        onDistractionDetected(packageName)
                        lastWarnedPackage = packageName
                    }
                }

                if (packageName != null && !distractionApps.contains(packageName)) {
                    lastWarnedPackage = null
                }

                monitoringHandler.postDelayed(this, 5_000)
            }
        }

        monitoringHandler.post(monitoringRunnable)
    }

    fun stopMonitoring() {
        isMonitoring = false
        lastWarnedPackage = null
        monitoringHandler.removeCallbacksAndMessages(null)
    }
}