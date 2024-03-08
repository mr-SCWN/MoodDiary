package edu.put.mooddiary

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LogPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_page)

        // Find the LogInButton by its ID and set an OnClickListener on it
        val logInButton: Button = findViewById(R.id.LogInButton)
        logInButton.setOnClickListener {
            // Intent to start MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
