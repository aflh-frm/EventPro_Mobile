package com.example.eventpro_mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.eventpro_mobile.data.Event
import com.example.eventpro_mobile.data.EventRepository
import com.example.eventpro_mobile.data.SessionManager
import com.example.eventpro_mobile.data.Ticket
import com.example.eventpro_mobile.data.TicketRepository
import com.example.eventpro_mobile.data.TicketStatus
import com.example.eventpro_mobile.databinding.ActivityDetailBinding
import com.example.eventpro_mobile.pembayaran.PembayaranActivity
import com.example.eventpro_mobile.tiket.ETiketActivity
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.loadEventImage
import com.example.eventpro_mobile.util.setLightStatusBar
import com.example.eventpro_mobile.util.toRupiah
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * 06 Detail Event.
 * Materi: Intent getIntExtra (P4), NestedScrollView (P5), AlertDialog & Snackbar (P4), Glide.
 */
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var event: Event
    private lateinit var session: SessionManager
    private lateinit var ticketRepository: TicketRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "DetailActivity dibuat")
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topBar.applySystemBarsPadding(bottom = false)
        binding.bottomBar.applySystemBarsPadding(top = false)
        setLightStatusBar(false)

        // Ambil id event yang dikirim lewat Intent
        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
        val found = EventRepository.getById(eventId)
        if (found == null) {
            Toast.makeText(this, "Event tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        event = found
        session = SessionManager(this)
        ticketRepository = TicketRepository(this)

        bindEvent()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnShare.setOnClickListener { shareEvent() }
        binding.btnFavorite.setOnClickListener {
            val favorite = session.toggleFavorite(event.id)
            updateFavoriteIcon()
            val message = if (favorite) "Ditambahkan ke favorit" else "Dihapus dari favorit"
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).setAnchorView(binding.bottomBar).show()
        }
        binding.btnBuy.setOnClickListener { onBuyClicked() }
    }

    override fun onResume() {
        super.onResume()
        // Status tiket bisa berubah setelah kembali dari halaman pembayaran
        if (::event.isInitialized) updateBuyButton()
    }

    private fun bindEvent() {
        binding.imgHeader.loadEventImage(event.imageUrl)
        binding.tvCategory.text = event.category
        binding.tvTitle.text = event.title
        binding.tvOrganizer.text = "oleh ${event.organizer}"
        binding.tvDate.text = event.dayDate
        binding.tvTime.text = event.time
        binding.tvLocation.text = event.location
        binding.tvLocationNote.text = event.locationNote
        binding.iconLocation.setImageResource(if (event.isOnline) R.drawable.ic_videocam else R.drawable.ic_location)
        binding.tvQuota.text = "Kuota ${event.quota} peserta"
        binding.tvRemaining.text = "Tersisa ${event.remaining} kursi"
        binding.tvDescription.text = event.description
        binding.tvPrice.text = event.price.toRupiah()
        updateFavoriteIcon()
    }

    private fun updateFavoriteIcon() {
        binding.btnFavorite.setImageResource(
            if (session.isFavorite(event.id)) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
        // ic_favorite punya warna merah sendiri, jadi tint hanya untuk ikon outline
        binding.btnFavorite.imageTintList = if (session.isFavorite(event.id)) null
        else getColorStateList(R.color.text_primary)
    }

    private fun updateBuyButton() {
        val ticket = ticketRepository.findByEvent(event.id)
        binding.btnBuy.isEnabled = true
        binding.btnBuy.text = when (ticket?.status) {
            null -> if (event.isFree) "Daftar Gratis" else "Beli Tiket"
            TicketStatus.MENUNGGU_PEMBAYARAN, TicketStatus.DITOLAK -> "Bayar Sekarang"
            TicketStatus.MENUNGGU_KONFIRMASI -> {
                binding.btnBuy.isEnabled = false
                "Menunggu Konfirmasi"
            }
            TicketStatus.TERDAFTAR -> "Lihat E-Tiket"
        }
    }

    private fun onBuyClicked() {
        val ticket = ticketRepository.findByEvent(event.id)
        if (ticket != null) {
            openTicketFlow(ticket)
            return
        }
        // Konfirmasi sebelum membuat tiket (materi P4: AlertDialog)
        MaterialAlertDialogBuilder(this)
            .setTitle("Konfirmasi Pendaftaran")
            .setMessage("Daftar ke \"${event.title}\" seharga ${event.price.toRupiah()}?")
            .setPositiveButton("Ya, Daftar") { dialog, _ ->
                dialog.dismiss()
                val newTicket = ticketRepository.create(event)
                if (event.isFree) {
                    Toast.makeText(this, "Pendaftaran berhasil! E-tiket sudah aktif.", Toast.LENGTH_SHORT).show()
                }
                openTicketFlow(newTicket)
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun openTicketFlow(ticket: Ticket) {
        val target = when (ticket.status) {
            TicketStatus.TERDAFTAR -> ETiketActivity::class.java
            TicketStatus.MENUNGGU_KONFIRMASI -> return
            else -> PembayaranActivity::class.java
        }
        val intent = Intent(this, target)
        intent.putExtra(EXTRA_TICKET_CODE, ticket.code)
        startActivity(intent)
    }

    private fun shareEvent() {
        val text = "Yuk ikut ${event.title}!\n${event.dayDate}, ${event.time}\n" +
            "${event.location}\nHarga: ${event.price.toRupiah()}\n\nDaftar lewat aplikasi EventPro."
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, "Bagikan event"))
    }

    companion object {
        const val EXTRA_EVENT_ID = "event_id"
        const val EXTRA_TICKET_CODE = "ticket_code"
    }
}
