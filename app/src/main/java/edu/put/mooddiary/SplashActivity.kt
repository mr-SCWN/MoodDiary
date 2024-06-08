package edu.put.mooddiary

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val imageView = findViewById<ImageView>(R.id.splash_logo)
        val spinAndGrow = AnimationUtils.loadAnimation(this, R.anim.spin_and_grow)
        imageView.startAnimation(spinAndGrow)

        Handler().postDelayed({
            val logIntent = Intent(this@SplashActivity, LogPage::class.java)
            startActivity(logIntent)
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

    companion object {
        private const val SPLASH_TIME_OUT = 3000 // 3 seconds
    }
}