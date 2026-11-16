package com.example.eventpro_mobile.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventpro_mobile.BaseActivity
import com.example.eventpro_mobile.DetailActivity
import com.example.eventpro_mobile.R
import com.example.eventpro_mobile.data.EventRepository
import com.example.eventpro_mobile.data.SessionManager
import com.example.eventpro_mobile.data.TicketRepository
import com.example.eventpro_mobile.databinding.FragmentHomeBinding
import com.example.eventpro_mobile.databinding.ItemCategoryChipBinding
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.initials
import com.example.eventpro_mobile.util.setLightStatusBar
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

/**
 * 04 Beranda Peserta.
 * Materi: Fragment + ViewBinding (P7), SharedPreferences (P6), GridLayout & Chip (P9),
 * Intent putExtra (P4), Glide (Custom Adapter), Snackbar (P4).
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var selectedCategory = EventRepository.CATEGORY_ALL

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.header.applySystemBarsPadding(bottom = false)

        // Nama user diambil dari SharedPreferences (tidak bergantung pada Intent)
        val name = SessionManager(requireContext()).currentName
        binding.tvAvatar.text = name.initials()
        binding.tvGreeting.text = "Halo, $name"

        binding.btnNotification.setOnClickListener {
            Snackbar.make(binding.root, "Belum ada notifikasi baru", Snackbar.LENGTH_SHORT).show()
        }

        // Pencarian: tekan tombol cari di keyboard atau tombol filter -> tab Jelajah
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                openJelajah(query = binding.etSearch.text.toString())
                true
            } else {
                false
            }
        }
        binding.btnFilter.setOnClickListener {
            openJelajah(query = binding.etSearch.text.toString())
        }

        binding.cardCariEvent.setOnClickListener { openJelajah() }
        binding.cardTiketSaya.setOnClickListener {
            (requireActivity() as BaseActivity).openTab(R.id.nav_tiket)
        }
        binding.tvSeeAll.setOnClickListener { openJelajah(category = selectedCategory) }

        setupCategoryChips()
        showLatestEvents()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().setLightStatusBar(false)
        val total = TicketRepository(requireContext()).getAll().size
        binding.tvTicketCount.text = if (total == 0) "Status & e-tiket" else "$total tiket tersimpan"
    }

    private fun setupCategoryChips() {
        EventRepository.categories.forEach { category ->
            val chip = ItemCategoryChipBinding.inflate(layoutInflater, binding.chipGroupCategory, false).root
            chip.id = View.generateViewId()
            chip.text = category
            binding.chipGroupCategory.addView(chip)
            if (category == selectedCategory) binding.chipGroupCategory.check(chip.id)
        }
        binding.chipGroupCategory.setOnCheckedStateChangeListener { group, checkedIds ->
            val chipId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            selectedCategory = group.findViewById<Chip>(chipId).text.toString()
            showLatestEvents()
        }
    }

    /** "Event Terbaru" dengan RecyclerView horizontal. */
    private fun showLatestEvents() {
        val events = EventRepository.getLatest(selectedCategory)
        binding.rvLatest.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvLatest.adapter = EventCardAdapter(events) { event -> openDetail(event.id) }
        binding.tvLatestEmpty.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun openDetail(eventId: Int) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_EVENT_ID, eventId)
        startActivity(intent)
    }

    private fun openJelajah(category: String? = null, query: String? = null) {
        (requireActivity() as BaseActivity).openJelajah(category, query?.takeIf { it.isNotBlank() })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
