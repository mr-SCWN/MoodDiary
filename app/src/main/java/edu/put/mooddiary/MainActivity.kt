package edu.put.mooddiary

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logOutButton: Button = findViewById(R.id.LogOutButton)
        logOutButton.setOnClickListener {
            // Intent to start MainActivity
            val intent = Intent(this, LogPage::class.java)
            startActivity(intent)
        }
        val calendarButton: Button = findViewById(R.id.CalendarButton)
        calendarButton.setOnClickListener {
            val intent = Intent (this, CalendarPage::class.java)
            startActivity(intent)
        }
        val raportButton: Button = findViewById(R.id.RaportButton)
        raportButton.setOnClickListener {
            val intent = Intent (this, RaportPage::class.java)
            startActivity(intent)
        }

    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "Orientation changed to Landscape", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "Orientation changed to Portrait", Toast.LENGTH_SHORT).show()
        }
    }
}