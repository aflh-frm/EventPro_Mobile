package com.example.eventpro_mobile.pembayaran

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventpro_mobile.BaseActivity
import com.example.eventpro_mobile.DetailActivity
import com.example.eventpro_mobile.R
import com.example.eventpro_mobile.data.Event
import com.example.eventpro_mobile.data.EventRepository
import com.example.eventpro_mobile.data.Ticket
import com.example.eventpro_mobile.data.TicketRepository
import com.example.eventpro_mobile.data.TicketStatus
import com.example.eventpro_mobile.databinding.ActivityPembayaranBinding
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.loadEventImage
import com.example.eventpro_mobile.util.setLightStatusBar
import com.example.eventpro_mobile.util.toRupiah
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * 08 Pembayaran Tiket: transfer manual + upload bukti.
 * Materi: CoordinatorLayout/AppBarLayout (P5), Intent extra (P4), AlertDialog & Snackbar (P4).
 */
class PembayaranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPembayaranBinding
    private lateinit var repository: TicketRepository
    private lateinit var ticket: Ticket
    private lateinit var event: Event
    private var proofUri: Uri? = null

    // Memilih foto dari galeri / file manager tanpa perlu izin penyimpanan
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) onImagePicked(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "PembayaranActivity dibuat")
        enableEdgeToEdge()
        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.appBar.applySystemBarsPadding(bottom = false)
        binding.scrollContent.applySystemBarsPadding(top = false)
        setLightStatusBar(true)

        repository = TicketRepository(this)
        val code = intent.getStringExtra(DetailActivity.EXTRA_TICKET_CODE).orEmpty()
        val foundTicket = repository.findByCode(code)
        val foundEvent = foundTicket?.let { EventRepository.getById(it.eventId) }
        if (foundTicket == null || foundEvent == null) {
            Toast.makeText(this, "Data tiket tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        ticket = foundTicket
        event = foundEvent

        // Toolbar sebagai Action Bar (materi P5)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Pembayaran Tiket"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }
        binding.btnCancel.setOnClickListener { finish() }

        binding.imgEvent.loadEventImage(event.imageUrl)
        binding.tvTitle.text = event.title
        binding.tvSchedule.text = "${event.shortDate} • ${event.location}"
        binding.tvTotal.text = event.price.toRupiah()
        binding.tvTicketCode.text = "Kode: ${ticket.code}"

        binding.btnCopy.setOnClickListener {
            val clipboard = getSystemService(ClipboardManager::class.java)
            val number = binding.tvAccountNumber.text.toString().replace(" ", "")
            clipboard.setPrimaryClip(ClipData.newPlainText("Nomor rekening", number))
            Snackbar.make(binding.root, "Nomor rekening disalin", Snackbar.LENGTH_SHORT).show()
        }

        binding.boxUpload.setOnClickListener { pickImage.launch("image/*") }

        binding.btnSubmit.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Kirim Bukti Pembayaran?")
                .setMessage("Pastikan nominal transfer ${event.price.toRupiah()} sudah sesuai.")
                .setPositiveButton("Kirim") { dialog, _ ->
                    dialog.dismiss()
                    submitProof()
                }
                .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun onImagePicked(uri: Uri) {
        val size = contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getLong(0) else 0L
        } ?: 0L

        if (size > MAX_SIZE_BYTES) {
            Snackbar.make(binding.root, "Ukuran foto melebihi 2 MB, pilih foto lain", Snackbar.LENGTH_LONG).show()
            return
        }

        proofUri = uri
        Glide.with(this).load(uri).centerCrop().into(binding.imgProof)
        binding.imgProof.visibility = View.VISIBLE
        binding.tvChangePhoto.visibility = View.VISIBLE
        binding.layoutUploadEmpty.visibility = View.GONE
        binding.btnSubmit.isEnabled = true
    }

    private fun submitProof() {
        repository.updateStatus(ticket.code, TicketStatus.MENUNGGU_KONFIRMASI, proofUri?.toString())

        // Kembali ke BaseActivity dan langsung buka tab Tiket dengan banner sukses
        val intent = Intent(this, BaseActivity::class.java)
            .putExtra(BaseActivity.EXTRA_TAB, BaseActivity.TAB_TIKET)
            .putExtra(BaseActivity.EXTRA_TICKET_SUCCESS, true)
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

    companion object {
        private const val MAX_SIZE_BYTES = 2L * 1024 * 1024
    }
}
