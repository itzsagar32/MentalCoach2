package com.krishu.mentalcoach

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var leisureCreditManager: LeisureCreditManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        leisureCreditManager = LeisureCreditManager(this)

        val leisureCreditText = findViewById<TextView>(R.id.dashboardLeisureCreditText)
        val rankText = findViewById<TextView>(R.id.dashboardRankText)
        val debtText = findViewById<TextView>(R.id.dashboardDebtText)
        val statusText = findViewById<TextView>(R.id.dashboardStatusText)

        val controlPanelButton = findViewById<Button>(R.id.openControlPanelButton)
        val focusPageButton = findViewById<Button>(R.id.openFocusPageButton)
        val distractionPageButton = findViewById<Button>(R.id.openDistractionPageButton)
        val missionsPageButton = findViewById<Button>(R.id.openMissionsPageButton)
        val reportsPageButton = findViewById<Button>(R.id.openReportsPageButton)
        val settingsPageButton = findViewById<Button>(R.id.openSettingsPageButton)

        updateDashboard(
            leisureCreditText = leisureCreditText,
            rankText = rankText,
            debtText = debtText,
            statusText = statusText
        )

        controlPanelButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        focusPageButton.setOnClickListener {
            startActivity(Intent(this, FocusActivity::class.java))
        }

        distractionPageButton.setOnClickListener {
            startActivity(Intent(this, DistractionActivity::class.java))
        }

        missionsPageButton.setOnClickListener {
            startActivity(Intent(this, MissionsActivity::class.java))
        }

        reportsPageButton.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }

        settingsPageButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        leisureCreditManager = LeisureCreditManager(this)

        val leisureCreditText = findViewById<TextView>(R.id.dashboardLeisureCreditText)
        val rankText = findViewById<TextView>(R.id.dashboardRankText)
        val debtText = findViewById<TextView>(R.id.dashboardDebtText)
        val statusText = findViewById<TextView>(R.id.dashboardStatusText)

        updateDashboard(
            leisureCreditText = leisureCreditText,
            rankText = rankText,
            debtText = debtText,
            statusText = statusText
        )
    }

    private fun updateDashboard(
        leisureCreditText: TextView,
        rankText: TextView,
        debtText: TextView,
        statusText: TextView
    ) {
        val credits = leisureCreditManager.getCreditsSeconds()
        val formattedCredits = leisureCreditManager.formatCredits(credits)

        leisureCreditText.text = "🎮 Leisure Credits: $formattedCredits"
        rankText.text = "🏅 Rank: Recruit"
        debtText.text = "🔥 Discipline Debt: 00:00"
        statusText.text = "🟠 Status: Awaiting orders"
    }
}