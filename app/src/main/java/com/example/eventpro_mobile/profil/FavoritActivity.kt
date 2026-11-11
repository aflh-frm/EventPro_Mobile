package com.example.eventpro_mobile.profil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.eventpro_mobile.DetailActivity
import com.example.eventpro_mobile.R
import com.example.eventpro_mobile.data.Event
import com.example.eventpro_mobile.data.EventRepository
import com.example.eventpro_mobile.data.SessionManager
import com.example.eventpro_mobile.databinding.ActivityFavoritBinding
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.setLightStatusBar

/**
 * Daftar event favorit.
 * Materi ListView: ArrayAdapter + layout bawaan android.R.layout.simple_list_item_1.
 */
class FavoritActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritBinding
    private var favoriteEvents: List<Event> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "FavoritActivity dibuat")
        enableEdgeToEdge()
        binding = ActivityFavoritBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.main.applySystemBarsPadding()
        setLightStatusBar(true)

        // Toolbar sebagai Action Bar (materi P5)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Event Favorit"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        // Aksi saat item di-list diklik
        binding.listFavorit.setOnItemClickListener { _, _, position, _ ->
            val selected = favoriteEvents[position]
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, selected.id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Dimuat ulang karena favorit bisa berubah dari halaman detail
        loadFavorites()
    }

    private fun loadFavorites() {
        val session = SessionManager(this)
        favoriteEvents = EventRepository.getAll().filter { session.isFavorite(it.id) }
        val dataList = favoriteEvents.map { it.title }

        // Adapter penghubung dataList dengan layout simple_list_item_1
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            dataList
        )
        binding.listFavorit.adapter = adapter

        binding.tvEmpty.visibility = if (dataList.isEmpty()) View.VISIBLE else View.GONE
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
