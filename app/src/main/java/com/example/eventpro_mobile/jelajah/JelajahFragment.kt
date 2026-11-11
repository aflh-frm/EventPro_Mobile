package com.example.eventpro_mobile.jelajah

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.eventpro_mobile.BaseActivity
import com.example.eventpro_mobile.DetailActivity
import com.example.eventpro_mobile.R
import com.example.eventpro_mobile.data.EventRepository
import com.example.eventpro_mobile.databinding.FragmentJelajahBinding
import com.example.eventpro_mobile.databinding.ItemCategoryChipBinding
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.setLightStatusBar
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * 05 Jelajah Event.
 * Materi: Toolbar + MenuProvider (P9), TextInputLayout clear_text (P9), ChipGroup (P9),
 * ListView + Custom Adapter, AlertDialog (P4), Intent putExtra (P4).
 */
class JelajahFragment : Fragment() {

    private var _binding: FragmentJelajahBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: EventListAdapter
    private var selectedCategory = EventRepository.CATEGORY_ALL
    private var priceFilter = EventRepository.PriceFilter.SEMUA

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJelajahBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.applySystemBarsPadding(bottom = false)

        // Ambil kategori / kata kunci yang dikirim dari Beranda
        val (category, query) = (requireActivity() as BaseActivity).consumePendingFilter()
        if (category != null) selectedCategory = category
        if (query != null) binding.etSearch.setText(query)

        // Option menu filter di Toolbar
        // Toolbar sebagai Action Bar (materi P7) + option menu dengan MenuProvider (materi P9)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Jelajah Event"

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_jelajah, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_filter -> {
                        showPriceFilterDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        adapter = EventListAdapter(requireContext()) { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
            startActivity(intent)
        }
        binding.listEvents.adapter = adapter

        setupCategoryChips()
        binding.etSearch.doAfterTextChanged { applyFilter() }
        applyFilter()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().setLightStatusBar(true)
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
            applyFilter()
        }
    }

    private fun showPriceFilterDialog() {
        val options = EventRepository.PriceFilter.entries
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Filter Harga")
            .setSingleChoiceItems(
                options.map { it.label }.toTypedArray(),
                options.indexOf(priceFilter)
            ) { dialog, which ->
                priceFilter = options[which]
                applyFilter()
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun applyFilter() {
        val result = EventRepository.search(
            query = binding.etSearch.text?.toString().orEmpty(),
            category = selectedCategory,
            priceFilter = priceFilter
        )
        adapter.submit(result)

        val filterInfo = if (priceFilter == EventRepository.PriceFilter.SEMUA) "" else " • ${priceFilter.label}"
        binding.tvResultInfo.text = "${result.size} event ditemukan$filterInfo"
        binding.layoutEmpty.visibility = if (result.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
