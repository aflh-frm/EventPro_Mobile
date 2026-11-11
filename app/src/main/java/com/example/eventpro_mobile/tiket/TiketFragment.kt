package com.example.eventpro_mobile.tiket

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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.eventpro_mobile.BaseActivity
import com.example.eventpro_mobile.DetailActivity
import com.example.eventpro_mobile.R
import com.example.eventpro_mobile.data.TicketRepository
import com.example.eventpro_mobile.data.TicketStatus
import com.example.eventpro_mobile.databinding.FragmentTiketBinding
import com.example.eventpro_mobile.pembayaran.PembayaranActivity
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.setLightStatusBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * 07 Tiket Saya.
 * Materi: ListView + Custom Adapter, MenuProvider (P9), AlertDialog & Snackbar (P4), Intent (P3/P4).
 */
class TiketFragment : Fragment() {

    private var _binding: FragmentTiketBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: TicketRepository
    private lateinit var adapter: TicketAdapter

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

        // Banner muncul jika baru saja mengirim bukti pembayaran
        if ((requireActivity() as BaseActivity).consumeTicketSuccess()) {
            binding.bannerSuccess.visibility = View.VISIBLE
        }
        binding.bannerSuccess.setOnClickListener { it.visibility = View.GONE }

        // Toolbar sebagai Action Bar (materi P7) + option menu dengan MenuProvider (materi P9)
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

        adapter = TicketAdapter(
            requireContext(),
            onPay = { ticket -> openWithTicket(PembayaranActivity::class.java, ticket.code) },
            onOpenTicket = { ticket -> openWithTicket(ETiketActivity::class.java, ticket.code) },
            onOpenEvent = { ticket ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_EVENT_ID, ticket.eventId)
                startActivity(intent)
            }
        )
        binding.listTickets.adapter = adapter

        binding.toggleStatus.addOnButtonCheckedListener { _, _, isChecked ->
            if (isChecked) loadTickets()
        }
        binding.btnExplore.setOnClickListener {
            (requireActivity() as BaseActivity).openTab(R.id.nav_jelajah)
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().setLightStatusBar(true)
        // Muat ulang karena status bisa berubah setelah pembayaran / konfirmasi
        loadTickets()
    }

    private fun loadTickets() {
        val all = repository.getAll()
        val filtered = when (binding.toggleStatus.checkedButtonId) {
            R.id.btnTabWaiting -> all.filter { it.status.isWaiting }
            R.id.btnTabRegistered -> all.filter { it.status == TicketStatus.TERDAFTAR }
            else -> all
        }
        adapter.submit(filtered)
        binding.layoutEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    /** Belum ada aplikasi EO, jadi konfirmasi pembayaran disimulasikan dari sini. */
    private fun confirmSimulation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Simulasi Konfirmasi EO")
            .setMessage("Semua tiket berstatus \"Menunggu Konfirmasi\" akan dikonfirmasi menjadi \"Terdaftar\".")
            .setPositiveButton("Konfirmasi") { dialog, _ ->
                val count = repository.confirmAllWaiting()
                loadTickets()
                val message = if (count == 0) "Tidak ada tiket yang menunggu konfirmasi"
                else "$count tiket berhasil dikonfirmasi"
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun openWithTicket(target: Class<*>, code: String) {
        val intent = Intent(requireContext(), target)
        intent.putExtra(DetailActivity.EXTRA_TICKET_CODE, code)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
