package com.example.eventpro_mobile.tiket

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.eventpro_mobile.data.EventRepository
import com.example.eventpro_mobile.data.Ticket
import com.example.eventpro_mobile.data.TicketStatus
import com.example.eventpro_mobile.databinding.ItemTicketBinding
import com.example.eventpro_mobile.util.applyStatusBadge
import com.example.eventpro_mobile.util.loadEventImage
import com.example.eventpro_mobile.util.toRupiah

/**
 * Custom Adapter daftar tiket. Tombol aksi menyesuaikan status:
 * Menunggu Pembayaran / Ditolak -> Bayar Sekarang, Terdaftar -> Download E-Tiket.
 */
class TicketAdapter(
    context: Context,
    private val onPay: (Ticket) -> Unit,
    private val onOpenTicket: (Ticket) -> Unit,
    private val onOpenEvent: (Ticket) -> Unit
) : ArrayAdapter<Ticket>(context, 0, mutableListOf()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            ItemTicketBinding.inflate(LayoutInflater.from(context), parent, false).also {
                it.root.tag = it
            }
        } else {
            convertView.tag as ItemTicketBinding
        }

        val ticket = getItem(position) ?: return binding.root
        val event = EventRepository.getById(ticket.eventId) ?: return binding.root

        binding.imgEvent.loadEventImage(event.imageUrl)
        binding.tvTitle.text = event.title
        binding.tvLocation.text = event.location
        binding.tvDate.text = event.shortDate
        binding.tvPrice.text = event.price.toRupiah()
        binding.tvStatus.applyStatusBadge(ticket.status)

        val status = ticket.status
        binding.btnPay.visibility =
            if (status == TicketStatus.MENUNGGU_PEMBAYARAN || status == TicketStatus.DITOLAK) View.VISIBLE else View.GONE
        binding.btnPay.text = if (status == TicketStatus.DITOLAK) "Upload Ulang" else "Bayar Sekarang"
        binding.btnDownload.visibility = if (status == TicketStatus.TERDAFTAR) View.VISIBLE else View.GONE
        binding.tvNote.visibility = if (status == TicketStatus.MENUNGGU_KONFIRMASI) View.VISIBLE else View.GONE

        binding.btnPay.setOnClickListener { onPay(ticket) }
        binding.btnDownload.setOnClickListener { onOpenTicket(ticket) }
        binding.root.setOnClickListener {
            if (status == TicketStatus.TERDAFTAR) onOpenTicket(ticket) else onOpenEvent(ticket)
        }
        return binding.root
    }

    fun submit(tickets: List<Ticket>) {
        clear()
        addAll(tickets)
        notifyDataSetChanged()
    }
}
