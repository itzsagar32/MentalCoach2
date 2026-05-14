package com.krishu.mentalcoach

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class DistractionMonitoringService : Service() {

    private lateinit var notificationHelper: CoachNotificationHelper
    private lateinit var appUsageMonitor: AppUsageMonitor
    private lateinit var distractionMonitorManager: DistractionMonitorManager

    override fun onCreate() {
        super.onCreate()

        notificationHelper = CoachNotificationHelper(this)
        notificationHelper.createNotificationChannel()

        appUsageMonitor = AppUsageMonitor(this)
        distractionMonitorManager = DistractionMonitorManager(appUsageMonitor)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundServiceNotification()
        startMonitoringDistractions()

        return START_STICKY
    }

    private fun startForegroundServiceNotification() {
        val notification = NotificationCompat.Builder(this, "discipline_alerts")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Mental Coach is active")
            .setContentText("Monitoring distractions. Stay disciplined.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        startForeground(2001, notification)
    }

    private fun startMonitoringDistractions() {
        if (!appUsageMonitor.hasUsageAccess()) {
            stopSelf()
            return
        }

        val distractionApps = loadDistractionApps()

        distractionMonitorManager.startMonitoring(
            distractionApps = distractionApps,
            onDistractionDetected = { packageName ->
                val warning = "Distraction detected: $packageName. Close it now."
                notificationHelper.showDisciplineNotification(warning)
            }
        )
    }

    private fun loadDistractionApps(): MutableList<String> {
        val sharedPreferences = getSharedPreferences("mental_coach_prefs", Context.MODE_PRIVATE)
        val savedSet = sharedPreferences.getStringSet("distraction_apps", emptySet())

        return savedSet?.toMutableList() ?: mutableListOf()
    }

    override fun onDestroy() {
        distractionMonitorManager.stopMonitoring()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}