package com.example.eventpro_mobile.data

/**
 * Status transaksi sesuai proses bisnis EventPro:
 * Pending (menunggu pembayaran / konfirmasi) -> Confirmed (terdaftar) / Rejected (ditolak).
 */
enum class TicketStatus(val label: String) {
    MENUNGGU_PEMBAYARAN("Menunggu Pembayaran"),
    MENUNGGU_KONFIRMASI("Menunggu Konfirmasi"),
    TERDAFTAR("Terdaftar"),
    DITOLAK("Ditolak");

    val isWaiting: Boolean
        get() = this == MENUNGGU_PEMBAYARAN || this == MENUNGGU_KONFIRMASI || this == DITOLAK
}

data class Ticket(
    val code: String,          // contoh: EVP-2026-00217
    val eventId: Int,
    val status: TicketStatus,
    val proofUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
