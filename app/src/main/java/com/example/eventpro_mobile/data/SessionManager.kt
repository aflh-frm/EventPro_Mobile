package com.example.eventpro_mobile.data

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject

/**
 * Pengelola SharedPreferences "user_pref" (materi P6).
 * Menyimpan status login, data user aktif, daftar akun terdaftar, onboarding, dan favorit.
 */
class SessionManager(context: Context) {

    data class Account(
        val name: String,
        val email: String,
        val password: String,
        val role: String
    )

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // ===== Status login =====
    val isLoggedIn: Boolean get() = prefs.getBoolean(KEY_IS_LOGIN, false)
    val currentName: String get() = prefs.getString(KEY_USERNAME, null) ?: "Peserta"
    val currentEmail: String? get() = prefs.getString(KEY_EMAIL, null)
    val currentRole: String get() = prefs.getString(KEY_ROLE, null) ?: ROLE_PESERTA
    val lastEmail: String? get() = prefs.getString(KEY_LAST_EMAIL, null)

    /** remember = checkbox "Ingat saya". Jika false, user harus login lagi saat aplikasi dibuka ulang. */
    fun login(account: Account, remember: Boolean) {
        prefs.edit {
            putBoolean(KEY_IS_LOGIN, remember)
            putString(KEY_USERNAME, account.name)
            putString(KEY_EMAIL, account.email)
            putString(KEY_ROLE, account.role)
            putString(KEY_LAST_EMAIL, account.email)
        }
    }

    /** Hanya menghapus data sesi (materi P6: editor.remove), akun & tiket tetap tersimpan. */
    fun logout() {
        prefs.edit {
            remove(KEY_IS_LOGIN)
            remove(KEY_USERNAME)
            remove(KEY_EMAIL)
            remove(KEY_ROLE)
        }
    }

    // ===== Akun =====
    fun register(account: Account): Boolean {
        val accounts = getAccounts()
        if (accounts.any { it.email.equals(account.email, ignoreCase = true) }) return false
        saveAccounts(accounts + account)
        return true
    }

    fun findAccount(email: String, password: String): Account? =
        getAccounts().find { it.email.equals(email, ignoreCase = true) && it.password == password }

    fun isDemoAccount(email: String?): Boolean = email.equals(DEMO_EMAIL, ignoreCase = true)

    private fun getAccounts(): List<Account> {
        val raw = prefs.getString(KEY_ACCOUNTS, null)
        val accounts = if (raw == null) {
            emptyList()
        } else {
            val array = JSONArray(raw)
            (0 until array.length()).map { i ->
                val o = array.getJSONObject(i)
                Account(o.getString("name"), o.getString("email"), o.getString("password"), o.getString("role"))
            }
        }

        // Akun demo selalu disesuaikan dengan DEMO_EMAIL & DEMO_PASSWORD terbaru,
        // jadi kalau konstanta diganti, data lama di HP ikut diperbarui.
        val demo = Account("Peserta Seminar", DEMO_EMAIL, DEMO_PASSWORD, ROLE_PESERTA)
        if (accounts.none { it.email.equals(DEMO_EMAIL, ignoreCase = true) && it.password == DEMO_PASSWORD }) {
            val updated = accounts.filterNot { it.email.equals(DEMO_EMAIL, ignoreCase = true) } + demo
            saveAccounts(updated)
            return updated
        }
        return accounts
    }

    private fun saveAccounts(accounts: List<Account>) {
        val array = JSONArray()
        accounts.forEach {
            array.put(
                JSONObject()
                    .put("name", it.name)
                    .put("email", it.email)
                    .put("password", it.password)
                    .put("role", it.role)
            )
        }
        prefs.edit { putString(KEY_ACCOUNTS, array.toString()) }
    }

    // ===== Onboarding =====
    var isTutorialSeen: Boolean
        get() = prefs.getBoolean(KEY_TUTORIAL_SEEN, false)
        set(value) = prefs.edit { putBoolean(KEY_TUTORIAL_SEEN, value) }

    // ===== Favorit =====
    fun isFavorite(eventId: Int): Boolean =
        prefs.getStringSet(favoriteKey(), emptySet())?.contains(eventId.toString()) == true

    /** Mengembalikan status favorit yang baru. */
    fun toggleFavorite(eventId: Int): Boolean {
        val set = prefs.getStringSet(favoriteKey(), emptySet())?.toMutableSet() ?: mutableSetOf()
        val id = eventId.toString()
        val nowFavorite = if (set.contains(id)) {
            set.remove(id); false
        } else {
            set.add(id); true
        }
        prefs.edit { putStringSet(favoriteKey(), set) }
        return nowFavorite
    }

    private fun favoriteKey() = "favorites_" + (currentEmail ?: "guest")

    companion object {
        const val PREF_NAME = "user_pref"
        const val KEY_IS_LOGIN = "isLogin"
        const val KEY_USERNAME = "username"
        const val KEY_EMAIL = "email"
        const val KEY_ROLE = "role"
        const val KEY_LAST_EMAIL = "last_email"
        const val KEY_ACCOUNTS = "accounts"
        const val KEY_TUTORIAL_SEEN = "tutorial_seen"

        const val ROLE_PESERTA = "Peserta"
        const val ROLE_EO = "Event Organizer"
        const val ROLE_ADMIN = "Admin"

        const val DEMO_EMAIL = "peserta@gmail.com"
        const val DEMO_PASSWORD = "peserta123"
    }
}
