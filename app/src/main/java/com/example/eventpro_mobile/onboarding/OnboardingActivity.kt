package com.example.eventpro_mobile.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.eventpro_mobile.BaseActivity
import com.example.eventpro_mobile.LoginActivity
import com.example.eventpro_mobile.data.SessionManager
import com.example.eventpro_mobile.databinding.ActivityOnboardingBinding
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.setLightStatusBar

/**
 * Onboarding 3 halaman (materi P9: ViewPager2 + DotsIndicator,
 * latihan 7.4 tombol Lewati/Mulai, latihan 7.5 tampil sekali dengan SharedPreferences).
 */
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "OnboardingActivity dibuat")
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.main.applySystemBarsPadding()
        setLightStatusBar(true)

        session = SessionManager(this)

        // 3 Fragment tutorial ditampilkan lewat ViewPager2
        val pages = listOf(
            Onboarding1Fragment(),
            Onboarding2Fragment(),
            Onboarding3Fragment()
        )

        binding.viewPagerOnboarding.adapter = OnboardingAdapter(this, pages)
        // attachTo dipanggil SETELAH adapter dipasang
        binding.dotIndicator.attachTo(binding.viewPagerOnboarding)

        binding.viewPagerOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val isLast = position == pages.lastIndex
                binding.btnNext.text = if (isLast) "MULAI SEKARANG" else "LANJUT"
                binding.btnSkip.visibility = if (isLast) View.INVISIBLE else View.VISIBLE
            }
        })

        binding.btnNext.setOnClickListener {
            val current = binding.viewPagerOnboarding.currentItem
            if (current < pages.lastIndex) {
                binding.viewPagerOnboarding.currentItem = current + 1
            } else {
                finishOnboarding()
            }
        }

        binding.btnSkip.setOnClickListener { finishOnboarding() }
    }

    private fun finishOnboarding() {
        session.isTutorialSeen = true
        // Jika dibuka ulang dari halaman Profil, cukup tutup halaman ini
        if (intent.getBooleanExtra(EXTRA_FROM_PROFILE, false)) {
            finish()
            return
        }
        val target = if (session.isLoggedIn) BaseActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, target))
        finish()
    }

    companion object {
        const val EXTRA_FROM_PROFILE = "from_profile"
    }
}
