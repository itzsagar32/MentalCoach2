package com.krishu.mentalcoach

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class DistractionMonitoringService : Service() {

    companion object {
        const val ACTION_STOP_MONITORING = "com.krishu.mentalcoach.STOP_MONITORING"
    }

    private lateinit var notificationHelper: CoachNotificationHelper
    private lateinit var appUsageMonitor: AppUsageMonitor
    private lateinit var distractionMonitorManager: DistractionMonitorManager
    private lateinit var leisureCreditManager: LeisureCreditManager

    override fun onCreate() {
        super.onCreate()

        notificationHelper = CoachNotificationHelper(this)
        notificationHelper.createNotificationChannel()

        appUsageMonitor = AppUsageMonitor(this)
        distractionMonitorManager = DistractionMonitorManager(appUsageMonitor)
        leisureCreditManager = LeisureCreditManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.action == ACTION_STOP_MONITORING) {
            stopSelf()
            return START_NOT_STICKY
        }

        startForegroundServiceNotification()
        startMonitoringDistractions()

        return START_STICKY
    }

    private fun startForegroundServiceNotification() {
        val stopIntent = Intent(this, DistractionMonitoringService::class.java).apply {
            action = ACTION_STOP_MONITORING
        }

        val stopPendingIntent = PendingIntent.getService(
            this,
            3001,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, "discipline_alerts")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Mental Coach is active")
            .setContentText("Monitoring distractions and leisure credits.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(
                R.mipmap.ic_launcher,
                "Stop",
                stopPendingIntent
            )
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
                handleBackgroundDistractionDetected(packageName)
            }
        )
    }

    private fun handleBackgroundDistractionDetected(packageName: String) {
        val spendAmountSeconds = 60L

        val hasEnoughCredits = leisureCreditManager.spendCredits(spendAmountSeconds)

        if (hasEnoughCredits) {
            val remainingCredits = leisureCreditManager.formatCredits(
                leisureCreditManager.getCreditsSeconds()
            )

            val message =
                "Authorized leisure: $packageName. 1 minute spent. Remaining: $remainingCredits."

            notificationHelper.showDisciplineNotification(message)
        } else {
            val warning =
                "UNAUTHORIZED DISTRACTION: $packageName. You have no leisure credits. yahi krta reh jayega jindagi bhar. bas sapno mein hi jeete reh, chutiye"

            notificationHelper.showDisciplineNotification(warning)
        }
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