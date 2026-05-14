package com.krishu.mentalcoach

import android.os.CountDownTimer

class FocusTimerManager {

    private var currentTimer: CountDownTimer? = null

    fun startTimer(
        modeName: String,
        durationMillis: Long,
        onTickUpdate: (String) -> Unit,
        onFinish: (String) -> Unit
    ) {
        currentTimer?.cancel()

        currentTimer = object : CountDownTimer(durationMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val totalSeconds = millisUntilFinished / 1000
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60

                val formattedTime = "Time left: %02d:%02d".format(minutes, seconds)
                onTickUpdate(formattedTime)
            }

            override fun onFinish() {
                currentTimer = null
                val message = "$modeName complete. Good. Now extend that discipline."
                onFinish(message)
            }

        }.start()
    }

    fun cancelTimer(onCancel: () -> Unit) {
        currentTimer?.cancel()
        currentTimer = null
        onCancel()
    }
}