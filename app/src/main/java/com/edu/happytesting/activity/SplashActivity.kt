package com.edu.happytesting.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.edu.happytesting.R
import com.edu.happytesting.databinding.ActivitySplashBinding
import com.edu.happytesting.preference.HappyPreference

class SplashActivity : AppCompatActivity() {
    private val splashScreen by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    @SuppressLint("MissingInflatedId")
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(splashScreen.root)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            )
        val backgroundImage: TextView = splashScreen.slideLeft
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.image_slider)
        backgroundImage.startAnimation(slideAnimation)
        login()
    }
    private fun login() {
        val loginId = HappyPreference(this).getUserDetails()["studentId"]
        Log.d("LoginId", loginId.toString())
        if (loginId=="null" || loginId.isNullOrBlank()) {
            Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            }, 3000)
        }
            else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

