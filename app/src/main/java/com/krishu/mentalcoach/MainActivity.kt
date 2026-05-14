package com.krishu.mentalcoach

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val coachCommands = listOf(
        "Stand up straight. Discipline starts now.",
        "Stop negotiating with laziness.",
        "One focused hour. No excuses.",
        "You are not tired. You are untrained.",
        "Put the phone down and execute."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val coachMessageText = findViewById<TextView>(R.id.coachMessageText)
        val getOrdersButton = findViewById<Button>(R.id.getOrdersButton)

        getOrdersButton.setOnClickListener {
            coachMessageText.text = coachCommands.random()
        }
    }
}