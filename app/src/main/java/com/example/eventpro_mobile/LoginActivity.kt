package com.example.eventpro_mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.eventpro_mobile.data.SessionManager
import com.example.eventpro_mobile.data.TicketRepository
import com.example.eventpro_mobile.databinding.ActivityLoginBinding
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.setLightStatusBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * 02 Login. Materi: ViewBinding, TextInputLayout (P9), SharedPreferences (P6),
 * AlertDialog & Snackbar (P4), Intent (P3).
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "LoginActivity dibuat")
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.main.applySystemBarsPadding(bottom = false)
        binding.sheet.applySystemBarsPadding(top = false, includeIme = true)
        setLightStatusBar(false)

        session = SessionManager(this)
        binding.etEmail.setText(intent.getStringExtra(EXTRA_EMAIL) ?: session.lastEmail.orEmpty())

        binding.btnLogin.setOnClickListener { attemptLogin() }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgot.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Lupa Password")
                .setMessage("Hubungi admin melalui admin@eventpro.id untuk mengatur ulang password akunmu.")
                .setPositiveButton("Mengerti") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        binding.btnGoogle.setOnClickListener {
            Snackbar.make(binding.root, "Masuk dengan Google belum tersedia", Snackbar.LENGTH_SHORT).show()
        }

        // Ketuk kotak akun demo untuk mengisi form otomatis
        binding.tvDemo.text = "Akun demo: ${SessionManager.DEMO_EMAIL}  •  ${SessionManager.DEMO_PASSWORD}"
        binding.tvDemo.setOnClickListener {
            binding.etEmail.setText(SessionManager.DEMO_EMAIL)
            binding.etPassword.setText(SessionManager.DEMO_PASSWORD)
        }
    }

    /** Dipanggil saat kembali dari RegisterActivity (FLAG_ACTIVITY_CLEAR_TOP). */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.getStringExtra(EXTRA_EMAIL)?.let {
            binding.etEmail.setText(it)
            binding.etPassword.text?.clear()
            binding.etPassword.requestFocus()
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        binding.tilEmail.error = null
        binding.tilPassword.error = null

        // Validasi input
        var valid = true
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email wajib diisi"
            valid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Format email tidak valid"
            valid = false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password wajib diisi"
            valid = false
        }
        if (!valid) return

        val account = session.findAccount(email, password)
        when {
            account == null -> showDialog("Login Gagal", "Email atau password salah. Silakan coba lagi.")

            // Untuk saat ini aplikasi hanya untuk Peserta
            account.role != SessionManager.ROLE_PESERTA -> showDialog(
                "Akun Tidak Didukung",
                "Saat ini EventPro hanya bisa digunakan oleh akun Peserta."
            )

            else -> {
                // Simpan sesi ke SharedPreferences (isLogin mengikuti checkbox "Ingat saya")
                session.login(account, binding.cbRemember.isChecked)
                if (session.isDemoAccount(account.email)) {
                    TicketRepository(this).seedDemoTickets()
                }
                Toast.makeText(this, "Selamat datang, ${account.name}!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, BaseActivity::class.java))
                finish()
            }
        }
    }

    private fun showDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    companion object {
        const val EXTRA_EMAIL = "email"
    }
}
