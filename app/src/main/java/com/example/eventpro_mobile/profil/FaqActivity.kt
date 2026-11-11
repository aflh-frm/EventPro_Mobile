package com.example.eventpro_mobile.profil

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.SimpleAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.eventpro_mobile.R
import com.example.eventpro_mobile.databinding.ActivityFaqBinding
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.setLightStatusBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * FAQ / Pertanyaan Umum.
 * Materi ListView: SimpleAdapter + layout bawaan android.R.layout.simple_list_item_2
 * (text1 = pertanyaan, text2 = jawaban singkat).
 */
class FaqActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaqBinding

    // Data berbentuk list of map: key "title" dan "desc"
    private val dataListWithDesc = listOf(
        mapOf(
            "title" to "Bagaimana cara mendaftar event?",
            "desc" to "Pilih event di Beranda atau Jelajah, buka detailnya, lalu tekan tombol Beli Tiket / Daftar Gratis."
        ),
        mapOf(
            "title" to "Bagaimana cara membayar tiket?",
            "desc" to "Transfer sesuai total tagihan ke rekening yang tertera, lalu unggah foto bukti transfer di halaman Pembayaran."
        ),
        mapOf(
            "title" to "Berapa lama pembayaran dikonfirmasi?",
            "desc" to "Event Organizer akan memeriksa bukti transfer. Status tiket berubah dari Menunggu Konfirmasi menjadi Terdaftar."
        ),
        mapOf(
            "title" to "Di mana saya melihat e-tiket?",
            "desc" to "Buka tab Tiket, pilih tiket berstatus Terdaftar, lalu tekan Download E-Tiket."
        ),
        mapOf(
            "title" to "Apakah e-tiket perlu dicetak?",
            "desc" to "Tidak perlu. Cukup tunjukkan QR code di aplikasi atau file PDF saat check-in."
        ),
        mapOf(
            "title" to "Bagaimana jika pembayaran ditolak?",
            "desc" to "Tiket berstatus Ditolak bisa dibayar ulang dengan menekan tombol Upload Ulang di halaman Tiket Saya."
        ),
        mapOf(
            "title" to "Bagaimana cara menyimpan event favorit?",
            "desc" to "Tekan ikon hati di halaman detail event. Daftar favorit ada di menu Profil > Event Favorit."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "FaqActivity dibuat")
        enableEdgeToEdge()
        binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.main.applySystemBarsPadding()
        setLightStatusBar(true)

        // Toolbar sebagai Action Bar (materi P5)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "FAQ"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        // SimpleAdapter: hubungkan key "title" & "desc" ke text1 & text2 milik simple_list_item_2
        val adapter = SimpleAdapter(
            this,
            dataListWithDesc,
            android.R.layout.simple_list_item_2,
            arrayOf("title", "desc"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        binding.listFaq.adapter = adapter

        // Aksi saat item di-list diklik: tampilkan jawaban lengkap
        binding.listFaq.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = dataListWithDesc[position]
            MaterialAlertDialogBuilder(this)
                .setTitle(selectedItem["title"])
                .setMessage(selectedItem["desc"])
                .setPositiveButton("Mengerti") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

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
