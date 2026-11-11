package com.example.eventpro_mobile.data

/**
 * Sumber data event. Belum ada backend, jadi data disimpan di dalam aplikasi
 * (sama seperti messageList di materi ListView Custom Adapter).
 */
object EventRepository {

    const val CATEGORY_ALL = "Semua"

    val categories = listOf(
        CATEGORY_ALL,
        "Teknologi & IT",
        "Bisnis & Marketing",
        "Kesehatan",
        "Musik & Hiburan",
        "Pendidikan"
    )

    enum class PriceFilter(val label: String) {
        SEMUA("Semua harga"),
        GRATIS("Gratis"),
        BERBAYAR("Berbayar")
    }

    private val events = listOf(
        Event(
            id = 1,
            title = "Workshop UI/UX Design 2025",
            category = "Teknologi & IT",
            organizer = "Panitia Event",
            dayDate = "Rabu, 15 Jan 2026",
            shortDate = "15 Jan 2026",
            time = "09:00 - 15:00 WIB",
            location = "Zoom Meeting",
            locationNote = "Link dikirim setelah pembayaran",
            isOnline = true,
            quota = 100,
            remaining = 42,
            price = 150000,
            description = "Pelajari cara mendesain aplikasi yang user-friendly langsung dari expert Google. " +
                "Materi meliputi riset pengguna, wireframe, prototyping di Figma, dan usability testing. " +
                "Peserta mendapatkan e-sertifikat dan file latihan.",
            imageUrl = "https://picsum.photos/seed/eventpro-uiux/600/400"
        ),
        Event(
            id = 2,
            title = "Strategi Digital Marketing UMKM",
            category = "Bisnis & Marketing",
            organizer = "Komunitas UMKM Riau",
            dayDate = "Sabtu, 10 Jan 2026",
            shortDate = "10 Jan 2026",
            time = "09:00 - 12:00 WIB",
            location = "Hotel Pangeran Pekanbaru",
            locationNote = "Jl. Jend. Sudirman No. 371, Pekanbaru",
            isOnline = false,
            quota = 80,
            remaining = 25,
            price = 75000,
            description = "Seminar praktis untuk pelaku UMKM: membangun brand di media sosial, " +
                "membuat konten yang menjual, dan memanfaatkan marketplace serta iklan berbayar " +
                "dengan budget terbatas.",
            imageUrl = "https://picsum.photos/seed/eventpro-marketing/600/400"
        ),
        Event(
            id = 3,
            title = "Webinar Mental Health",
            category = "Kesehatan",
            organizer = "Psikolog Muda Indonesia",
            dayDate = "Selasa, 20 Jan 2026",
            shortDate = "20 Jan 2026",
            time = "19:00 - 21:00 WIB",
            location = "Google Meet",
            locationNote = "Link dikirim setelah pendaftaran",
            isOnline = true,
            quota = 300,
            remaining = 120,
            price = 0,
            description = "Mengenali tanda burnout dan stres pada mahasiswa serta pekerja muda, " +
                "lengkap dengan tips menjaga kesehatan mental bersama psikolog klinis.",
            imageUrl = "https://picsum.photos/seed/eventpro-mental/600/400"
        ),
        Event(
            id = 4,
            title = "Konser Amal: Suara untuk Negeri",
            category = "Musik & Hiburan",
            organizer = "BEM PCR",
            dayDate = "Minggu, 25 Jan 2026",
            shortDate = "25 Jan 2026",
            time = "18:30 - 22:00 WIB",
            location = "Gelanggang Remaja Pekanbaru",
            locationNote = "Pintu masuk dibuka pukul 17:30",
            isOnline = false,
            quota = 500,
            remaining = 210,
            price = 50000,
            description = "Konser amal yang menampilkan band kampus dan musisi lokal. " +
                "Seluruh keuntungan disalurkan untuk beasiswa pendidikan anak di daerah 3T.",
            imageUrl = "https://picsum.photos/seed/eventpro-konser/600/400"
        ),
        Event(
            id = 5,
            title = "Bootcamp Fullstack Laravel",
            category = "Pendidikan",
            organizer = "Coding Camp Pekanbaru",
            dayDate = "Rabu, 04 Feb 2026",
            shortDate = "04 Feb 2026",
            time = "08:00 - 16:00 WIB",
            location = "Politeknik Caltex Riau",
            locationNote = "Gedung Utama Lt. 2, Lab Komputer",
            isOnline = false,
            quota = 40,
            remaining = 9,
            price = 500000,
            description = "Bootcamp intensif membangun aplikasi web dari nol dengan Laravel: " +
                "routing, Eloquent ORM, autentikasi, REST API, sampai deploy ke server.",
            imageUrl = "https://picsum.photos/seed/eventpro-laravel/600/400"
        ),
        Event(
            id = 6,
            title = "Seminar Nasional AI untuk Pendidikan",
            category = "Teknologi & IT",
            organizer = "Prodi Teknik Informatika PCR",
            dayDate = "Kamis, 12 Feb 2026",
            shortDate = "12 Feb 2026",
            time = "08:30 - 13:00 WIB",
            location = "Auditorium PCR",
            locationNote = "Politeknik Caltex Riau, Rumbai",
            isOnline = false,
            quota = 250,
            remaining = 87,
            price = 100000,
            description = "Bagaimana kecerdasan buatan mengubah cara belajar dan mengajar. " +
                "Menghadirkan pembicara dari kampus dan industri teknologi.",
            imageUrl = "https://picsum.photos/seed/eventpro-ai/600/400"
        ),
        Event(
            id = 7,
            title = "Kelas Public Speaking",
            category = "Pendidikan",
            organizer = "Komunitas Bicara",
            dayDate = "Rabu, 18 Feb 2026",
            shortDate = "18 Feb 2026",
            time = "19:30 - 21:00 WIB",
            location = "Zoom Meeting",
            locationNote = "Link dikirim setelah pembayaran",
            isOnline = true,
            quota = 60,
            remaining = 31,
            price = 35000,
            description = "Latihan berbicara di depan umum: mengatasi gugup, menyusun materi, " +
                "dan teknik presentasi yang meyakinkan untuk sidang maupun interview.",
            imageUrl = "https://picsum.photos/seed/eventpro-speaking/600/400"
        ),
        Event(
            id = 8,
            title = "Talkshow Startup Founder",
            category = "Bisnis & Marketing",
            organizer = "Riau Startup Hub",
            dayDate = "Sabtu, 28 Feb 2026",
            shortDate = "28 Feb 2026",
            time = "13:00 - 16:00 WIB",
            location = "Co-working Space Pekanbaru",
            locationNote = "Jl. Arifin Ahmad No. 10, Pekanbaru",
            isOnline = false,
            quota = 120,
            remaining = 64,
            price = 0,
            description = "Cerita para founder startup lokal tentang membangun produk, " +
                "mencari pendanaan, dan bertahan di tahun-tahun pertama.",
            imageUrl = "https://picsum.photos/seed/eventpro-startup/600/400"
        )
    )

    fun getAll(): List<Event> = events

    /** Event terbaru untuk beranda (urut dari yang paling dekat tanggalnya). */
    fun getLatest(category: String = CATEGORY_ALL): List<Event> =
        events.filter { category == CATEGORY_ALL || it.category == category }.take(5)

    fun getById(id: Int): Event? = events.find { it.id == id }

    fun search(
        query: String,
        category: String = CATEGORY_ALL,
        priceFilter: PriceFilter = PriceFilter.SEMUA
    ): List<Event> {
        val keyword = query.trim().lowercase()
        return events.filter { event ->
            val matchQuery = keyword.isEmpty() ||
                event.title.lowercase().contains(keyword) ||
                event.location.lowercase().contains(keyword) ||
                event.organizer.lowercase().contains(keyword)
            val matchCategory = category == CATEGORY_ALL || event.category == category
            val matchPrice = when (priceFilter) {
                PriceFilter.SEMUA -> true
                PriceFilter.GRATIS -> event.isFree
                PriceFilter.BERBAYAR -> !event.isFree
            }
            matchQuery && matchCategory && matchPrice
        }
    }
}
