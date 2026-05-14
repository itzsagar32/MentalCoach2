package com.krishu.mentalcoach

import android.content.Context

class LeisureCreditManager(private val context: Context) {

    private val prefsName = "mental_coach_prefs"
    private val leisureCreditsKey = "leisure_credits_seconds"

    fun getCreditsSeconds(): Long {
        val sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        return sharedPreferences.getLong(leisureCreditsKey, 0L)
    }

    fun addCredits(secondsToAdd: Long) {
        val currentCredits = getCreditsSeconds()
        val newCredits = currentCredits + secondsToAdd

        val sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putLong(leisureCreditsKey, newCredits)
            .apply()
    }

    fun spendCredits(secondsToSpend: Long): Boolean {
        val currentCredits = getCreditsSeconds()

        if (currentCredits < secondsToSpend) {
            return false
        }

        val newCredits = currentCredits - secondsToSpend

        val sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putLong(leisureCreditsKey, newCredits)
            .apply()

        return true
    }

    fun resetCredits() {
        val sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putLong(leisureCreditsKey, 0L)
            .apply()
    }

    fun formatCredits(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60

        return "%02d:%02d".format(minutes, remainingSeconds)
    }
}