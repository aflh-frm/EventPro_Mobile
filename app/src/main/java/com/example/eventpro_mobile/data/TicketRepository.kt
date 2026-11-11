package com.example.eventpro_mobile.data

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

/**
 * Menyimpan tiket milik user yang sedang login di SharedPreferences (format JSON).
 * Setiap akun punya daftar tiket sendiri berdasarkan email.
 */
class TicketRepository(context: Context) {

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val session = SessionManager(context)

    private val storageKey: String
        get() = "tickets_" + (session.currentEmail ?: "guest")

    fun getAll(): List<Ticket> {
        val raw = prefs.getString(storageKey, null) ?: return emptyList()
        val array = JSONArray(raw)
        val result = mutableListOf<Ticket>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            result.add(
                Ticket(
                    code = obj.getString("code"),
                    eventId = obj.getInt("eventId"),
                    status = TicketStatus.valueOf(obj.getString("status")),
                    proofUri = if (obj.isNull("proofUri")) null else obj.getString("proofUri"),
                    createdAt = obj.optLong("createdAt", 0L)
                )
            )
        }
        return result.sortedByDescending { it.createdAt }
    }

    fun findByCode(code: String): Ticket? = getAll().find { it.code == code }

    fun findByEvent(eventId: Int): Ticket? = getAll().find { it.eventId == eventId }

    /** Membuat tiket baru. Event gratis langsung TERDAFTAR, berbayar menunggu pembayaran. */
    fun create(event: Event): Ticket {
        val ticket = Ticket(
            code = nextCode(),
            eventId = event.id,
            status = if (event.isFree) TicketStatus.TERDAFTAR else TicketStatus.MENUNGGU_PEMBAYARAN
        )
        save(getAll() + ticket)
        return ticket
    }

    fun updateStatus(code: String, status: TicketStatus, proofUri: String? = null) {
        val updated = getAll().map {
            if (it.code == code) it.copy(status = status, proofUri = proofUri ?: it.proofUri) else it
        }
        save(updated)
    }

    /** Simulasi validasi EO: semua tiket yang menunggu konfirmasi menjadi TERDAFTAR. */
    fun confirmAllWaiting(): Int {
        val tickets = getAll()
        val count = tickets.count { it.status == TicketStatus.MENUNGGU_KONFIRMASI }
        save(tickets.map {
            if (it.status == TicketStatus.MENUNGGU_KONFIRMASI) it.copy(status = TicketStatus.TERDAFTAR) else it
        })
        return count
    }

    /** Data contoh untuk akun demo agar halaman Tiket Saya tidak kosong saat presentasi. */
    fun seedDemoTickets() {
        if (prefs.contains(storageKey)) return
        val now = System.currentTimeMillis()
        save(
            listOf(
                Ticket(nextCode(), 1, TicketStatus.MENUNGGU_PEMBAYARAN, createdAt = now),
                Ticket(nextCode(), 2, TicketStatus.TERDAFTAR, createdAt = now - 60_000),
                Ticket(nextCode(), 3, TicketStatus.TERDAFTAR, createdAt = now - 120_000)
            )
        )
    }

    private fun save(tickets: List<Ticket>) {
        val array = JSONArray()
        tickets.forEach { t ->
            array.put(
                JSONObject()
                    .put("code", t.code)
                    .put("eventId", t.eventId)
                    .put("status", t.status.name)
                    .put("proofUri", t.proofUri ?: JSONObject.NULL)
                    .put("createdAt", t.createdAt)
            )
        }
        prefs.edit { putString(storageKey, array.toString()) }
    }

    private fun nextCode(): String {
        val next = prefs.getInt(KEY_COUNTER, 216) + 1
        prefs.edit { putInt(KEY_COUNTER, next) }
        return String.format(Locale.US, "EVP-2026-%05d", next)
    }

    companion object {
        private const val PREF_NAME = "ticket_pref"
        private const val KEY_COUNTER = "ticket_counter"
    }
}
