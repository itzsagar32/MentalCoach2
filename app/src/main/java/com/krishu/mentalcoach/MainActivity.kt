package com.krishu.mentalcoach

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val coachMessageGenerator = CoachMessageGenerator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val coachMessageText = findViewById<TextView>(R.id.coachMessageText)

        val generalButton = findViewById<Button>(R.id.generalButton)
        val distractionButton = findViewById<Button>(R.id.distractionButton)
        val studyButton = findViewById<Button>(R.id.studyButton)
        val workoutButton = findViewById<Button>(R.id.workoutButton)
        val emergencyButton = findViewById<Button>(R.id.emergencyButton)

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
    }
}