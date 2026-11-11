package com.example.eventpro_mobile.util

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import com.bumptech.glide.Glide
import com.example.eventpro_mobile.R
import com.example.eventpro_mobile.data.TicketStatus
import java.text.NumberFormat
import java.util.Locale

/** 150000 -> "Rp 150.000", 0 -> "GRATIS" */
fun Int.toRupiah(): String {
    if (this == 0) return "GRATIS"
    val format = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
    return "Rp " + format.format(this)
}

/**
 * Menambahkan padding sesuai status bar / navigation bar (pengganti kode insets bawaan
 * agar header berwarna bisa tampil sampai belakang status bar).
 */
fun View.applySystemBarsPadding(
    top: Boolean = true,
    bottom: Boolean = true,
    includeIme: Boolean = false
) {
    val startLeft = paddingLeft
    val startTop = paddingTop
    val startRight = paddingRight
    val startBottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        val bottomInset = if (includeIme) maxOf(bars.bottom, ime.bottom) else bars.bottom
        v.setPadding(
            startLeft + bars.left,
            startTop + if (top) bars.top else 0,
            startRight + bars.right,
            startBottom + if (bottom) bottomInset else 0
        )
        insets
    }
    doOnAttach { ViewCompat.requestApplyInsets(it) }
}

/** true = ikon status bar gelap (untuk latar terang), false = ikon putih (untuk header biru). */
fun Activity.setLightStatusBar(light: Boolean) {
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = light
}

/** Memuat gambar event dari URL dengan Glide (materi Custom Adapter). */
fun ImageView.loadEventImage(url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.bg_image_placeholder)
        .error(R.drawable.bg_image_placeholder)
        .centerCrop()
        .into(this)
}

/** Mengatur tampilan badge status tiket (warna latar + warna teks). */
fun TextView.applyStatusBadge(status: TicketStatus) {
    text = status.label
    val (bg, color) = when (status) {
        TicketStatus.TERDAFTAR -> R.drawable.bg_badge_success to R.color.color_success
        TicketStatus.DITOLAK -> R.drawable.bg_badge_error to R.color.color_error
        else -> R.drawable.bg_badge_warning to R.color.color_warning
    }
    setBackgroundResource(bg)
    setTextColor(ContextCompat.getColor(context, color))
}

/** Inisial nama untuk avatar, contoh "Peserta Seminar" -> "PS". */
fun String.initials(): String =
    trim().split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "EP" }
