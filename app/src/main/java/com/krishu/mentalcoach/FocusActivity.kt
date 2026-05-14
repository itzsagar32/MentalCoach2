package com.krishu.mentalcoach

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FocusActivity : AppCompatActivity() {

    private val focusTimerManager = FocusTimerManager()
    private lateinit var leisureCreditManager: LeisureCreditManager
    private lateinit var notificationHelper: CoachNotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focus)

        leisureCreditManager = LeisureCreditManager(this)
        notificationHelper = CoachNotificationHelper(this)
        notificationHelper.createNotificationChannel()

        val focusMessageText = findViewById<TextView>(R.id.focusMessageText)
        val focusTimerText = findViewById<TextView>(R.id.focusTimerText)
        val focusLeisureCreditText = findViewById<TextView>(R.id.focusLeisureCreditText)

        val quickResetButton = findViewById<Button>(R.id.focusQuickResetButton)
        val miniFocusButton = findViewById<Button>(R.id.focusMiniButton)
        val deepFocusButton = findViewById<Button>(R.id.focusDeepButton)
        val cancelButton = findViewById<Button>(R.id.focusCancelButton)

        updateLeisureCreditText(focusLeisureCreditText)

        quickResetButton.setOnClickListener {
            startFocusMode(
                focusMessageText = focusMessageText,
                focusTimerText = focusTimerText,
                focusLeisureCreditText = focusLeisureCreditText,
                modeName = "Quick Reset",
                durationMillis = 10_000
            )
        }

        miniFocusButton.setOnClickListener {
            startFocusMode(
                focusMessageText = focusMessageText,
                focusTimerText = focusTimerText,
                focusLeisureCreditText = focusLeisureCreditText,
                modeName = "Mini Focus",
                durationMillis = 60_000
            )
        }

        deepFocusButton.setOnClickListener {
            startFocusMode(
                focusMessageText = focusMessageText,
                focusTimerText = focusTimerText,
                focusLeisureCreditText = focusLeisureCreditText,
                modeName = "Deep Focus",
                durationMillis = 25 * 60 * 1000L
            )
        }

        cancelButton.setOnClickListener {
            focusTimerManager.cancelTimer {
                focusMessageText.text = "Timer cancelled. Regroup and restart."
                focusTimerText.text = "No active timer"
            }
        }
    }

    override fun onResume() {
        super.onResume()

        leisureCreditManager = LeisureCreditManager(this)
        val focusLeisureCreditText = findViewById<TextView>(R.id.focusLeisureCreditText)
        updateLeisureCreditText(focusLeisureCreditText)
    }

    private fun startFocusMode(
        focusMessageText: TextView,
        focusTimerText: TextView,
        focusLeisureCreditText: TextView,
        modeName: String,
        durationMillis: Long
    ) {
        focusMessageText.text = "$modeName started. Earn your leisure."

        focusTimerManager.startTimer(
            modeName = modeName,
            durationMillis = durationMillis,
            onTickUpdate = { timeText ->
                focusTimerText.text = timeText
            },
            onFinish = {
                val earnedSeconds = durationMillis / 1000 / 2

                leisureCreditManager.addCredits(earnedSeconds)
                updateLeisureCreditText(focusLeisureCreditText)

                val earnedText = leisureCreditManager.formatCredits(earnedSeconds)
                val message = "$modeName complete. You earned $earnedText leisure time."

                focusMessageText.text = message
                focusTimerText.text = "Timer complete"
                notificationHelper.showDisciplineNotification(message)
            }
        )
    }

    private fun updateLeisureCreditText(focusLeisureCreditText: TextView) {
        val credits = leisureCreditManager.getCreditsSeconds()
        val formattedCredits = leisureCreditManager.formatCredits(credits)

        focusLeisureCreditText.text = "🎮 Leisure Credits: $formattedCredits"
    }

    override fun onDestroy() {
        super.onDestroy()
        focusTimerManager.cancelTimer {
            // no UI update needed when activity is destroyed
        }
    }
}