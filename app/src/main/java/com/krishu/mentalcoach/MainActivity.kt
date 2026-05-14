package com.krishu.mentalcoach

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val coachMessageGenerator = CoachMessageGenerator()
    private val focusTimerManager = FocusTimerManager()
    private lateinit var notificationHelper: CoachNotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationHelper = CoachNotificationHelper(this)
        notificationHelper.createNotificationChannel()
        requestNotificationPermissionIfNeeded()

        val coachMessageText = findViewById<TextView>(R.id.coachMessageText)
        val timerText = findViewById<TextView>(R.id.timerText)

        val generalButton = findViewById<Button>(R.id.generalButton)
        val distractionButton = findViewById<Button>(R.id.distractionButton)
        val studyButton = findViewById<Button>(R.id.studyButton)
        val workoutButton = findViewById<Button>(R.id.workoutButton)
        val emergencyButton = findViewById<Button>(R.id.emergencyButton)
        val testNotificationButton = findViewById<Button>(R.id.testNotificationButton)

        val quickResetButton = findViewById<Button>(R.id.quickResetButton)
        val miniFocusButton = findViewById<Button>(R.id.miniFocusButton)
        val deepFocusButton = findViewById<Button>(R.id.deepFocusButton)
        val cancelTimerButton = findViewById<Button>(R.id.cancelTimerButton)

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

        testNotificationButton.setOnClickListener {
            val message = coachMessageGenerator.getDistractionWarning()
            notificationHelper.showDisciplineNotification(message)
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
}