package com.krishu.mentalcoach

class CoachMessageGenerator {

    private val generalCommands = listOf(
        "Stand up straight. Discipline starts now.",
        "Stop negotiating with laziness.",
        "One focused hour. No excuses.",
        "You are not tired. You are untrained.",
        "Put the phone down and execute."
    )

    private val distractionWarnings = listOf(
        "You opened a distraction. Close it now.",
        "That app is stealing your future. Shut it down.",
        "You are not resting. You are escaping.",
        "Scrolling is not recovery. Get back to your mission.",
        "Every wasted minute trains weakness. Stop now."
    )

    private val studyCommands = listOf(
        "Open your notes. Ten minutes. Start now.",
        "Your exam will not care about your excuses.",
        "One page. One concept. One victory.",
        "Study now so your future self does not suffer.",
        "Focus on the next paragraph. That is your battlefield."
    )

    private val workoutCommands = listOf(
        "Twenty pushups. No drama.",
        "Move your body. Wake your mind.",
        "You want confidence? Earn it with sweat.",
        "Stand up. Stretch. Breathe. Attack the day.",
        "Discipline is physical too. Move."
    )

    private val emergencyCommands = listOf(
        "Stop everything. Breathe. Reset. You are still in control.",
        "This is the danger zone. Put the phone down now.",
        "You are slipping. Do not panic. Take command.",
        "Lock in for five minutes. Win this moment.",
        "You do not need motivation. You need action."
    )

    fun getGeneralCommand(): String {
        return generalCommands.random()
    }

    fun getDistractionWarning(): String {
        return distractionWarnings.random()
    }

    fun getStudyCommand(): String {
        return studyCommands.random()
    }

    fun getWorkoutCommand(): String {
        return workoutCommands.random()
    }

    fun getEmergencyCommand(): String {
        return emergencyCommands.random()
    }
}