package com.example.eventpro_mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.eventpro_mobile.data.SessionManager
import com.example.eventpro_mobile.databinding.ActivityRegisterBinding
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.setLightStatusBar

/** 03 Daftar Akun (khusus Peserta). Akun disimpan di SharedPreferences lewat SessionManager. */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "RegisterActivity dibuat")
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.main.applySystemBarsPadding(includeIme = true)
        setLightStatusBar(true)

        // Toolbar sebagai Action Bar (materi P5)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }
        binding.tvLogin.setOnClickListener { finish() }

        binding.btnRegister.setOnClickListener { attemptRegister() }
    }

    private fun attemptRegister() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm = binding.etConfirm.text.toString()

        binding.tilName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirm.error = null

        var valid = true
        if (name.isEmpty()) {
            binding.tilName.error = "Nama wajib diisi"; valid = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Format email tidak valid"; valid = false
        }
        if (password.length < 8) {
            binding.tilPassword.error = "Password minimal 8 karakter"; valid = false
        }
        if (confirm != password) {
            binding.tilConfirm.error = "Konfirmasi password tidak sama"; valid = false
        }
        if (!valid) return

        val success = SessionManager(this).register(
            SessionManager.Account(name, email, password, SessionManager.ROLE_PESERTA)
        )
        if (!success) {
            binding.tilEmail.error = "Email sudah terdaftar"
            return
        }

        Toast.makeText(this, "Akun berhasil dibuat, silakan masuk", Toast.LENGTH_SHORT).show()
        // Kembali ke LoginActivity yang sudah ada di back stack dan isi email otomatis
        val intent = Intent(this, LoginActivity::class.java)
            .putExtra(LoginActivity.EXTRA_EMAIL, email)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    // Tombol back di Toolbar (materi P5: onOptionsItemSelected + android.R.id.home)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
