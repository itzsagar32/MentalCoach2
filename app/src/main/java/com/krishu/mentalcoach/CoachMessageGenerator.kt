package com.krishu.mentalcoach

class CoachMessageGenerator {

    private val commands = listOf(
        "Stand up straight. Discipline starts now.",
        "Stop negotiating with laziness.",
        "One focused hour. No excuses.",
        "You are not tired. You are untrained.",
        "Put the phone down and execute.",
        "Your future self is watching. Do not embarrass him.",
        "Comfort is the enemy. Move."
    )

    fun getRandomCommand(): String {
        return commands.random()
    }
}