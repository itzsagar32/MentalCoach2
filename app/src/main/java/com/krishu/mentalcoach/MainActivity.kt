package com.krishu.mentalcoach

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val coachMessageGenerator = CoachMessageGenerator()
    private lateinit var notificationHelper: CoachNotificationHelper

    private var currentTimer: CountDownTimer? = null

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
            startFocusTimer(
                coachMessageText = coachMessageText,
                timerText = timerText,
                modeName = "Quick Reset",
                durationMillis = 10_000
            )
        }

        miniFocusButton.setOnClickListener {
            startFocusTimer(
                coachMessageText = coachMessageText,
                timerText = timerText,
                modeName = "Mini Focus",
                durationMillis = 60_000
            )
        }

        deepFocusButton.setOnClickListener {
            startFocusTimer(
                coachMessageText = coachMessageText,
                timerText = timerText,
                modeName = "Deep Focus",
                durationMillis = 25 * 60 * 1000L
            )
        }

        cancelTimerButton.setOnClickListener {
            cancelFocusTimer(
                coachMessageText = coachMessageText,
                timerText = timerText
            )
        }
    }

    private fun startFocusTimer(
        coachMessageText: TextView,
        timerText: TextView,
        modeName: String,
        durationMillis: Long
    ) {
        currentTimer?.cancel()

        coachMessageText.text = "$modeName started. Hold the line."

        currentTimer = object : CountDownTimer(durationMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val totalSeconds = millisUntilFinished / 1000
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60

                timerText.text = "Time left: %02d:%02d".format(minutes, seconds)
            }

            override fun onFinish() {
                val message = "$modeName complete. Good. Now extend that discipline."
                coachMessageText.text = message
                timerText.text = "Timer complete"
                notificationHelper.showDisciplineNotification(message)

                currentTimer = null
            }

        }.start()
    }

    private fun cancelFocusTimer(
        coachMessageText: TextView,
        timerText: TextView
    ) {
        currentTimer?.cancel()
        currentTimer = null

        coachMessageText.text = "Timer cancelled. Regroup and restart with discipline."
        timerText.text = "No active timer"
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