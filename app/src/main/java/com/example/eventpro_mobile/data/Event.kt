package com.example.eventpro_mobile.data

/**
 * Model data event (materi P9: data class sebagai model untuk adapter).
 */
data class Event(
    val id: Int,
    val title: String,
    val category: String,
    val organizer: String,
    val dayDate: String,       // contoh: "Rabu, 15 Jan 2026"
    val shortDate: String,     // contoh: "15 Jan 2026"
    val time: String,          // contoh: "09:00 - 15:00 WIB"
    val location: String,      // contoh: "Zoom Meeting" atau "Hotel Pangeran Pekanbaru"
    val locationNote: String,  // keterangan kecil di bawah lokasi
    val isOnline: Boolean,
    val quota: Int,
    val remaining: Int,
    val price: Int,            // 0 berarti gratis
    val description: String,
    val imageUrl: String
) {
    val isFree: Boolean get() = price == 0
}
