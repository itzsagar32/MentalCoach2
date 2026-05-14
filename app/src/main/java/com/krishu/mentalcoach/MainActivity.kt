package com.krishu.mentalcoach

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val coachMessageGenerator = CoachMessageGenerator()
    private val focusTimerManager = FocusTimerManager()

    private lateinit var notificationHelper: CoachNotificationHelper
    private lateinit var appUsageMonitor: AppUsageMonitor
    private lateinit var distractionMonitorManager: DistractionMonitorManager
    private lateinit var installedAppReader: InstalledAppReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationHelper = CoachNotificationHelper(this)
        notificationHelper.createNotificationChannel()
        requestNotificationPermissionIfNeeded()

        appUsageMonitor = AppUsageMonitor(this)
        distractionMonitorManager = DistractionMonitorManager(appUsageMonitor)
        installedAppReader = InstalledAppReader(this)

        val coachMessageText = findViewById<TextView>(R.id.coachMessageText)
        val timerText = findViewById<TextView>(R.id.timerText)

        val generalButton = findViewById<Button>(R.id.generalButton)
        val distractionButton = findViewById<Button>(R.id.distractionButton)
        val studyButton = findViewById<Button>(R.id.studyButton)
        val workoutButton = findViewById<Button>(R.id.workoutButton)
        val emergencyButton = findViewById<Button>(R.id.emergencyButton)

        val quickResetButton = findViewById<Button>(R.id.quickResetButton)
        val miniFocusButton = findViewById<Button>(R.id.miniFocusButton)
        val deepFocusButton = findViewById<Button>(R.id.deepFocusButton)
        val cancelTimerButton = findViewById<Button>(R.id.cancelTimerButton)

        val distractionInput = findViewById<EditText>(R.id.distractionInput)
        val saveDistractionButton = findViewById<Button>(R.id.saveDistractionButton)
        val removeDistractionButton = findViewById<Button>(R.id.removeDistractionButton)
        val distractionListText = findViewById<TextView>(R.id.distractionListText)

        val grantUsageAccessButton = findViewById<Button>(R.id.grantUsageAccessButton)
        val checkCurrentAppButton = findViewById<Button>(R.id.checkCurrentAppButton)
        val startMonitoringButton = findViewById<Button>(R.id.startMonitoringButton)
        val stopMonitoringButton = findViewById<Button>(R.id.stopMonitoringButton)
        val startServiceMonitoringButton = findViewById<Button>(R.id.startServiceMonitoringButton)
        val stopServiceMonitoringButton = findViewById<Button>(R.id.stopServiceMonitoringButton)
        val testNotificationButton = findViewById<Button>(R.id.testNotificationButton)

        val loadInstalledAppsButton = findViewById<Button>(R.id.loadInstalledAppsButton)
        val installedAppsText = findViewById<TextView>(R.id.installedAppsText)
        val installedAppsListView = findViewById<ListView>(R.id.installedAppsListView)

        val distractionApps = loadDistractionApps()
        updateDistractionListText(distractionListText, distractionApps)

        generalButton.setOnClickListener {
            coachMessageText.text = coachMessageGenerator.getGeneralCommand()
        }

        distractionButton.setOnClickListener {
            coachMessageText.text = coachMessageGenerator.getDistractionWarning()
        }

        studyButton.setOnClickListener {
            coachMessageText.text = coachMessageGenerator.getStudyCommand()
        }

        workoutButton.setOnClickListener {
            coachMessageText.text = coachMessageGenerator.getWorkoutCommand()
        }

        emergencyButton.setOnClickListener {
            coachMessageText.text = coachMessageGenerator.getEmergencyCommand()
        }

        quickResetButton.setOnClickListener {
            startFocusMode(
                coachMessageText = coachMessageText,
                timerText = timerText,
                modeName = "Quick Reset",
                durationMillis = 10_000
            )
        }

        miniFocusButton.setOnClickListener {
            startFocusMode(
                coachMessageText = coachMessageText,
                timerText = timerText,
                modeName = "Mini Focus",
                durationMillis = 60_000
            )
        }

        deepFocusButton.setOnClickListener {
            startFocusMode(
                coachMessageText = coachMessageText,
                timerText = timerText,
                modeName = "Deep Focus",
                durationMillis = 25 * 60 * 1000L
            )
        }

        cancelTimerButton.setOnClickListener {
            focusTimerManager.cancelTimer {
                coachMessageText.text = "Timer cancelled. Regroup and restart with discipline."
                timerText.text = "No active timer"
            }
        }

        saveDistractionButton.setOnClickListener {
            val appName = distractionInput.text.toString().trim()

            if (appName.isNotEmpty()) {
                if (!distractionApps.contains(appName)) {
                    distractionApps.add(appName)
                    saveDistractionApps(distractionApps)
                    updateDistractionListText(distractionListText, distractionApps)

                    coachMessageText.text = "$appName added to distraction list."
                } else {
                    coachMessageText.text = "$appName is already on the distraction list."
                }

                distractionInput.text.clear()
            } else {
                coachMessageText.text = "Type an app package name first, soldier."
            }
        }

        removeDistractionButton.setOnClickListener {
            val appName = distractionInput.text.toString().trim()

            if (appName.isNotEmpty()) {
                if (distractionApps.contains(appName)) {
                    distractionApps.remove(appName)
                    saveDistractionApps(distractionApps)
                    updateDistractionListText(distractionListText, distractionApps)

                    coachMessageText.text = "$appName removed from distraction list."
                } else {
                    coachMessageText.text = "$appName is not on the distraction list."
                }

                distractionInput.text.clear()
            } else {
                coachMessageText.text = "Type an app package name to remove, soldier."
            }
        }

        loadInstalledAppsButton.setOnClickListener {
            val installedApps = installedAppReader.getInstalledApps()

            if (installedApps.isEmpty()) {
                installedAppsText.text = "No installed apps found."
                coachMessageText.text = "Could not load installed apps."
                return@setOnClickListener
            }

            installedAppsText.text = "Tap an app below to add it to your distraction list."

            val appDisplayList = installedApps.map { app ->
                "${app.appName}\n${app.packageName}"
            }

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                appDisplayList
            )

            installedAppsListView.adapter = adapter

            installedAppsListView.setOnItemClickListener { _, _, position, _ ->
                val selectedApp = installedApps[position]
                val packageName = selectedApp.packageName

                if (!distractionApps.contains(packageName)) {
                    distractionApps.add(packageName)
                    saveDistractionApps(distractionApps)
                    updateDistractionListText(distractionListText, distractionApps)

                    coachMessageText.text = "${selectedApp.appName} added to distraction list."

                    Toast.makeText(
                        this,
                        "Added: ${selectedApp.appName}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    coachMessageText.text =
                        "${selectedApp.appName} is already on the distraction list."

                    Toast.makeText(
                        this,
                        "Already added",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        grantUsageAccessButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
        }

        checkCurrentAppButton.setOnClickListener {
            Toast.makeText(this, "Checking current app...", Toast.LENGTH_SHORT).show()

            if (!appUsageMonitor.hasUsageAccess()) {
                val message = "Usage Access is NOT granted. Tap Grant Usage Access first."
                coachMessageText.text = message
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val recentPackages = appUsageMonitor.getRecentExternalPackageNames()

            if (recentPackages.isEmpty()) {
                val message =
                    "No recent external apps detected. Open YouTube or Chrome for 10 seconds, then return."
                coachMessageText.text = message
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val packageName = recentPackages.last()

            if (distractionApps.contains(packageName)) {
                val warning = "Distraction detected: $packageName. Close it now."
                coachMessageText.text = warning
                notificationHelper.showDisciplineNotification(warning)
            } else {
                coachMessageText.text =
                    "Last external app: $packageName\n\nRecent apps:\n" +
                            recentPackages.joinToString(separator = "\n") { app ->
                                "• $app"
                            }
            }
        }

        startMonitoringButton.setOnClickListener {
            if (!appUsageMonitor.hasUsageAccess()) {
                coachMessageText.text = "Grant Usage Access before starting monitoring."
                Toast.makeText(this, "Usage Access needed first", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            coachMessageText.text = "Monitoring started. Stay sharp."

            distractionMonitorManager.startMonitoring(
                distractionApps = distractionApps,
                onDistractionDetected = { packageName ->
                    val warning = "Distraction detected: $packageName. Close it now."
                    coachMessageText.text = warning
                    notificationHelper.showDisciplineNotification(warning)
                }
            )
        }

        stopMonitoringButton.setOnClickListener {
            distractionMonitorManager.stopMonitoring()
            coachMessageText.text = "Monitoring stopped."
        }

        startServiceMonitoringButton.setOnClickListener {
            if (!appUsageMonitor.hasUsageAccess()) {
                coachMessageText.text = "Grant Usage Access before starting background monitoring."
                Toast.makeText(this, "Usage Access needed first", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val serviceIntent = Intent(this, DistractionMonitoringService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }

            coachMessageText.text = "Background monitoring started."
        }

        stopServiceMonitoringButton.setOnClickListener {
            val serviceIntent = Intent(this, DistractionMonitoringService::class.java)
            stopService(serviceIntent)

            coachMessageText.text = "Background monitoring stopped."
        }

        testNotificationButton.setOnClickListener {
            val message = coachMessageGenerator.getDistractionWarning()
            notificationHelper.showDisciplineNotification(message)
        }
    }

    private fun startFocusMode(
        coachMessageText: TextView,
        timerText: TextView,
        modeName: String,
        durationMillis: Long
    ) {
        coachMessageText.text = "$modeName started. Hold the line."

        focusTimerManager.startTimer(
            modeName = modeName,
            durationMillis = durationMillis,
            onTickUpdate = { timeText ->
                timerText.text = timeText
            },
            onFinish = { finishMessage ->
                coachMessageText.text = finishMessage
                timerText.text = "Timer complete"
                notificationHelper.showDisciplineNotification(finishMessage)
            }
        )
    }

    private fun saveDistractionApps(distractionApps: List<String>) {
        val sharedPreferences = getSharedPreferences("mental_coach_prefs", Context.MODE_PRIVATE)

        sharedPreferences.edit()
            .putStringSet("distraction_apps", distractionApps.toSet())
            .apply()
    }

    private fun loadDistractionApps(): MutableList<String> {
        val sharedPreferences = getSharedPreferences("mental_coach_prefs", Context.MODE_PRIVATE)

        val savedSet = sharedPreferences.getStringSet("distraction_apps", emptySet())

        return savedSet?.toMutableList() ?: mutableListOf()
    }

    private fun updateDistractionListText(
        distractionListText: TextView,
        distractionApps: List<String>
    ) {
        if (distractionApps.isEmpty()) {
            distractionListText.text = "No distractions saved yet."
        } else {
            distractionListText.text = distractionApps.joinToString(separator = "\n") { app ->
                "• $app"
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!permissionGranted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        distractionMonitorManager.stopMonitoring()
    }
}