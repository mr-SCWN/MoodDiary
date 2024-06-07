package edu.put.mooddiary

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class LogPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_page)

        // Log In Button
        val logInButton: Button = findViewById(R.id.LogInButton)
        logInButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Sign Up Button
        val signUpButton: Button = findViewById(R.id.SignUpButton)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Set up fingerprint authentication
        setupFingerprintAuthentication()
    }

    private fun setupFingerprintAuthentication() {
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            val executor: Executor = ContextCompat.getMainExecutor(this)
            val biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        // Handle error
                        Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        // Open MainActivity on successful authentication
                        val intent = Intent(this@LogPage, MainActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        // Handle failed authentication
                        Toast.makeText(applicationContext, "Authentication failed, please try again.", Toast.LENGTH_SHORT).show()
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for MainActivity")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build()

            val fingerprintIcon: ImageView = findViewById(R.id.fingerprintIcon)
            fingerprintIcon.setOnClickListener {
                biometricPrompt.authenticate(promptInfo)
            }
        } else {
            // Handle case where biometric authentication is not available
            Toast.makeText(applicationContext, "Biometric authentication is not available on this device.", Toast.LENGTH_SHORT).show()
        }
    }
}
