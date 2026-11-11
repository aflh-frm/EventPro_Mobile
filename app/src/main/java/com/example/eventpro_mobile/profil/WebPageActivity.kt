package com.example.eventpro_mobile.profil

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.eventpro_mobile.R
import com.example.eventpro_mobile.databinding.ActivityWebPageBinding
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.setLightStatusBar

/**
 * Halaman web di dalam aplikasi (materi P5: WebView, toolbar hide/show saat scroll,
 * tombol back kembali ke halaman web sebelumnya).
 */
class WebPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "WebPageActivity dibuat")
        enableEdgeToEdge()
        binding = ActivityWebPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.main.applySystemBarsPadding()
        setLightStatusBar(true)

        // Toolbar sebagai Action Bar (materi P5)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = intent.getStringExtra(EXTRA_TITLE) ?: "EventPro"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                binding.progress.progress = newProgress
                binding.progress.visibility = if (newProgress < 100) View.VISIBLE else View.GONE
            }
        }
        binding.webView.loadUrl(intent.getStringExtra(EXTRA_URL) ?: "https://pcr.ac.id")

        // Toolbar sembunyi saat scroll ke bawah, muncul saat scroll ke atas
        binding.webView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.appBar.setExpanded(false, true)
            } else if (scrollY < oldScrollY) {
                binding.appBar.setExpanded(true, true)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
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

    companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_URL = "url"
    }
}
