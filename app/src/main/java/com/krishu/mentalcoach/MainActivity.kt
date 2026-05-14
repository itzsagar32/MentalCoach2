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
        val getOrdersButton = findViewById<Button>(R.id.getOrdersButton)

        getOrdersButton.setOnClickListener {
            coachMessageText.text = coachMessageGenerator.getRandomCommand()
        }
    }
}