package com.example.eventpro_mobile.tiket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.eventpro_mobile.BaseActivity
import com.example.eventpro_mobile.R
import com.example.eventpro_mobile.data.TicketRepository
import com.example.eventpro_mobile.databinding.FragmentTiketBinding
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.setLightStatusBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

/**
 * 07 Tiket Saya.
 * Materi Pertemuan 10: TabLayout + ViewPager2 + TabLayoutMediator, ikon dan badge pada tab.
 * Isi tiap tab: TicketListFragment (ListView + Custom Adapter).
 */
class TiketFragment : Fragment() {

    private var _binding: FragmentTiketBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: TicketRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTiketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.applySystemBarsPadding(bottom = false)
        repository = TicketRepository(requireContext())

        // Toolbar sebagai Action Bar + option menu dengan MenuProvider
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Tiket Saya"

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_tiket, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_confirm_simulation -> {
                        confirmSimulation()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Banner muncul jika baru saja mengirim bukti pembayaran
        val fromPayment = (requireActivity() as BaseActivity).consumeTicketSuccess()
        if (fromPayment) binding.bannerSuccess.visibility = View.VISIBLE
        binding.bannerSuccess.setOnClickListener { it.visibility = View.GONE }

        // 1. Inisialisasi adapter dan pasang ke ViewPager2
        binding.viewPager.adapter = TiketTabsAdapter(this)

        // 2. Hubungkan TabLayout dan ViewPager2, atur judul + ikon setiap tab
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Semua"
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_ticket)
                }
                1 -> {
                    tab.text = "Menunggu"
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_info_outline)
                }
                2 -> {
                    tab.text = "Terdaftar"
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_circle)
                }
            }
        }.attach()

        // Setelah kirim bukti pembayaran, langsung buka tab Menunggu
        if (fromPayment) binding.viewPager.setCurrentItem(1, false)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().setLightStatusBar(true)
        updateBadge()
    }

    /** Badge angka di tab Menunggu = jumlah tiket yang masih menunggu. */
    private fun updateBadge() {
        val tabMenunggu = binding.tabLayout.getTabAt(1) ?: return
        val waiting = repository.getAll().count { it.status.isWaiting }
        if (waiting > 0) {
            val badge = tabMenunggu.getOrCreateBadge()
            badge.isVisible = true
            badge.number = waiting
            badge.backgroundColor = ContextCompat.getColor(requireContext(), R.color.color_warning)
        } else {
            tabMenunggu.removeBadge()
        }
    }

    /** Belum ada aplikasi EO, jadi konfirmasi pembayaran disimulasikan dari sini. */
    private fun confirmSimulation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Simulasi Konfirmasi EO")
            .setMessage("Semua tiket berstatus \"Menunggu Konfirmasi\" akan dikonfirmasi menjadi \"Terdaftar\".")
            .setPositiveButton("Konfirmasi") { dialog, _ ->
                val count = repository.confirmAllWaiting()
                // Muat ulang isi semua tab yang sedang ada
                childFragmentManager.fragments
                    .filterIsInstance<TicketListFragment>()
                    .forEach { it.reload() }
                updateBadge()
                val message = if (count == 0) "Tidak ada tiket yang menunggu konfirmasi"
                else "$count tiket berhasil dikonfirmasi"
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
