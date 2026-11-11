package com.example.eventpro_mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.eventpro_mobile.data.SessionManager
import com.example.eventpro_mobile.databinding.ActivitySplashScreenBinding
import com.example.eventpro_mobile.onboarding.OnboardingActivity
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.setLightStatusBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 01 Splash Screen (materi P6).
 * Selain branding, splash juga mengecek sesi: onboarding -> login -> beranda.
 */
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "SplashScreenActivity dibuat")
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.main.applySystemBarsPadding()
        setLightStatusBar(false)

        lifecycleScope.launch {
            delay(2000) // simulasi memuat data selama 2 detik

            val session = SessionManager(this@SplashScreenActivity)
            val target = when {
                !session.isTutorialSeen -> OnboardingActivity::class.java  // pertama kali buka aplikasi
                session.isLoggedIn -> BaseActivity::class.java             // isLogin = true
                else -> LoginActivity::class.java                          // isLogin = false
            }
            startActivity(Intent(this@SplashScreenActivity, target))
            finish()
        }
    }
}
