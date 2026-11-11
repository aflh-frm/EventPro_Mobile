package com.example.eventpro_mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.eventpro_mobile.databinding.ActivityBaseBinding
import com.example.eventpro_mobile.home.HomeFragment
import com.example.eventpro_mobile.jelajah.JelajahFragment
import com.example.eventpro_mobile.profil.ProfilFragment
import com.example.eventpro_mobile.tiket.TiketFragment

/**
 * Activity utama dengan Bottom Navigation: Beranda, Jelajah, Tiket, Profil (materi P7).
 */
class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding

    // Data titipan dari fragment lain (mis. Beranda -> Jelajah membawa kata kunci)
    private var pendingCategory: String? = null
    private var pendingQuery: String? = null
    private var pendingTicketSuccess = false
    private var hasNewIntent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "BaseActivity dibuat")
        enableEdgeToEdge()
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inset atas ditangani masing-masing fragment, inset bawah oleh BottomNavigationView
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, 0, bars.right, 0)
            insets
        }

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_beranda -> replaceFragment(HomeFragment())
                R.id.nav_jelajah -> replaceFragment(JelajahFragment())
                R.id.nav_tiket -> replaceFragment(TiketFragment())
                R.id.nav_profil -> replaceFragment(ProfilFragment())
                else -> return@setOnItemSelectedListener false
            }
            true
        }
        // Menekan tab yang sedang aktif tidak memuat ulang halaman
        binding.bottomNavView.setOnItemReselectedListener { }

        if (savedInstanceState == null) {
            handleIntent(intent)
        }
    }

    /**
     * Dipanggil saat BaseActivity dibuka lagi dari Pembayaran (FLAG_ACTIVITY_CLEAR_TOP).
     * Pergantian fragment ditunda sampai onResume agar tidak error setelah onSaveInstanceState.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        hasNewIntent = true
    }

    override fun onResume() {
        super.onResume()
        if (hasNewIntent) {
            hasNewIntent = false
            handleIntent(intent)
        }
    }

    private fun handleIntent(intent: Intent) {
        pendingTicketSuccess = intent.getBooleanExtra(EXTRA_TICKET_SUCCESS, false)
        val tab = when (intent.getStringExtra(EXTRA_TAB)) {
            TAB_TIKET -> R.id.nav_tiket
            TAB_JELAJAH -> R.id.nav_jelajah
            TAB_PROFIL -> R.id.nav_profil
            else -> R.id.nav_beranda
        }
        openTab(tab, forceReload = true)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            // .addToBackStack(null) sengaja tidak dipakai: back langsung keluar aplikasi
            .commit()
    }

    /** Pindah tab dari dalam fragment. */
    fun openTab(@IdRes tabId: Int, forceReload: Boolean = false) {
        if (binding.bottomNavView.selectedItemId == tabId) {
            if (!forceReload && supportFragmentManager.findFragmentById(binding.fragmentContainer.id) != null) return
            val fragment = when (tabId) {
                R.id.nav_jelajah -> JelajahFragment()
                R.id.nav_tiket -> TiketFragment()
                R.id.nav_profil -> ProfilFragment()
                else -> HomeFragment()
            }
            replaceFragment(fragment)
        } else {
            binding.bottomNavView.selectedItemId = tabId
        }
    }

    /** Beranda -> Jelajah dengan membawa kategori / kata kunci pencarian. */
    fun openJelajah(category: String? = null, query: String? = null) {
        pendingCategory = category
        pendingQuery = query
        openTab(R.id.nav_jelajah, forceReload = true)
    }

    fun consumePendingFilter(): Pair<String?, String?> {
        val result = pendingCategory to pendingQuery
        pendingCategory = null
        pendingQuery = null
        return result
    }

    fun consumeTicketSuccess(): Boolean {
        val result = pendingTicketSuccess
        pendingTicketSuccess = false
        return result
    }

    companion object {
        const val EXTRA_TAB = "tab"
        const val EXTRA_TICKET_SUCCESS = "ticket_success"
        const val TAB_TIKET = "tiket"
        const val TAB_JELAJAH = "jelajah"
        const val TAB_PROFIL = "profil"
    }
}
