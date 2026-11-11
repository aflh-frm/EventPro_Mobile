package com.example.eventpro_mobile.tiket

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.eventpro_mobile.DetailActivity
import com.example.eventpro_mobile.R
import com.example.eventpro_mobile.data.Event
import com.example.eventpro_mobile.data.EventRepository
import com.example.eventpro_mobile.data.SessionManager
import com.example.eventpro_mobile.data.Ticket
import com.example.eventpro_mobile.data.TicketRepository
import com.example.eventpro_mobile.data.TicketStatus
import com.example.eventpro_mobile.databinding.ActivityETiketBinding
import com.example.eventpro_mobile.util.applyStatusBadge
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.setLightStatusBar
import com.example.eventpro_mobile.util.toRupiah
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

/**
 * 09 E-Tiket: QR code (ZXing) + simpan sebagai PDF ke folder Download.
 * Hanya bisa dibuka untuk tiket berstatus TERDAFTAR (Confirmed).
 */
class ETiketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityETiketBinding
    private lateinit var ticket: Ticket
    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "ETiketActivity dibuat")
        enableEdgeToEdge()
        binding = ActivityETiketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.content.applySystemBarsPadding()
        setLightStatusBar(false)

        val code = intent.getStringExtra(DetailActivity.EXTRA_TICKET_CODE).orEmpty()
        val foundTicket = TicketRepository(this).findByCode(code)
        val foundEvent = foundTicket?.let { EventRepository.getById(it.eventId) }
        if (foundTicket == null || foundEvent == null || foundTicket.status != TicketStatus.TERDAFTAR) {
            Toast.makeText(this, "E-tiket belum tersedia", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        ticket = foundTicket
        event = foundEvent

        // Toolbar sebagai Action Bar (materi P5)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "E-Tiket"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        val session = SessionManager(this)
        binding.tvStatus.applyStatusBadge(ticket.status)
        binding.tvTitle.text = event.title
        binding.tvDate.text = event.shortDate
        binding.tvTime.text = event.time
        binding.tvLocation.text = event.location
        binding.tvPrice.text = event.price.toRupiah()
        binding.tvCode.text = ticket.code
        binding.tvOwner.text = "${session.currentName} • ${session.currentEmail.orEmpty()}"
        binding.imgQr.setImageBitmap(generateQrCode("${ticket.code}|${event.id}|${session.currentEmail}", 600))

        binding.btnDownload.setOnClickListener { savePdf() }
    }

    // Option menu di Toolbar (materi P5: onCreateOptionsMenu)
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_e_tiket, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.action_share -> {
                shareTicket()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /** Membuat bitmap QR Code hitam-putih dari teks. */
    private fun generateQrCode(text: String, size: Int): Bitmap {
        val matrix = QRCodeWriter().encode(
            text, BarcodeFormat.QR_CODE, size, size, mapOf(EncodeHintType.MARGIN to 1)
        )
        val pixels = IntArray(size * size) { index ->
            if (matrix.get(index % size, index / size)) Color.BLACK else Color.WHITE
        }
        return Bitmap.createBitmap(pixels, size, size, Bitmap.Config.ARGB_8888)
    }

    /** Menggambar kartu tiket ke PdfDocument lalu menyimpannya ke Download/EventPro. */
    private fun savePdf() {
        val card = binding.cardTicket
        val pdf = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(card.width, card.height, 1).create()
        val page = pdf.startPage(pageInfo)
        page.canvas.drawColor(Color.WHITE)
        card.draw(page.canvas)
        pdf.finishPage(page)

        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "E-Tiket-${ticket.code}.pdf")
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/EventPro")
        }

        try {
            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("Gagal membuat file")
            contentResolver.openOutputStream(uri)?.use { pdf.writeTo(it) }
            Snackbar.make(binding.root, "E-tiket tersimpan di Download/EventPro", Snackbar.LENGTH_LONG)
                .setAction("Buka") { openPdf(uri) }
                .show()
        } catch (e: Exception) {
            Snackbar.make(binding.root, "Gagal menyimpan PDF: ${e.message}", Snackbar.LENGTH_LONG).show()
        } finally {
            pdf.close()
        }
    }

    private fun openPdf(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Tidak ada aplikasi pembuka PDF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareTicket() {
        val text = "E-Tiket EventPro\n${event.title}\n${event.dayDate}, ${event.time}\n" +
            "${event.location}\nKode tiket: ${ticket.code}"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, "Bagikan e-tiket"))
    }
}
