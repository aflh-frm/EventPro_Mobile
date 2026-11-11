package com.example.eventpro_mobile.profil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.example.eventpro_mobile.BaseActivity
import com.example.eventpro_mobile.LoginActivity
import com.example.eventpro_mobile.R
import com.example.eventpro_mobile.data.SessionManager
import com.example.eventpro_mobile.data.TicketRepository
import com.example.eventpro_mobile.data.TicketStatus
import com.example.eventpro_mobile.databinding.FragmentProfilBinding
import com.example.eventpro_mobile.databinding.ItemProfileMenuBinding
import com.example.eventpro_mobile.onboarding.OnboardingActivity
import com.example.eventpro_mobile.util.applySystemBarsPadding
import com.example.eventpro_mobile.util.initials
import com.example.eventpro_mobile.util.setLightStatusBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Profil Peserta: data dari SharedPreferences (P6), menu ke WebView (P5),
 * tutorial (P9), dan Logout dengan AlertDialog (P4/P6).
 */
class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var session: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.header.applySystemBarsPadding(bottom = false)
        session = SessionManager(requireContext())

        binding.tvAvatar.text = session.currentName.initials()
        binding.tvName.text = session.currentName
        binding.tvEmail.text = session.currentEmail.orEmpty()
        binding.tvRole.text = session.currentRole.uppercase()

        setupMenu(binding.menuTiket, R.drawable.ic_ticket, "Tiket Saya") {
            (requireActivity() as BaseActivity).openTab(R.id.nav_tiket)
        }
        setupMenu(binding.menuFavorit, R.drawable.ic_favorite_border, "Event Favorit") {
            startActivity(Intent(requireContext(), FavoritActivity::class.java))
        }
        setupMenu(binding.menuTutorial, R.drawable.ic_book, "Lihat Tutorial Aplikasi") {
            val intent = Intent(requireContext(), OnboardingActivity::class.java)
            intent.putExtra(OnboardingActivity.EXTRA_FROM_PROFILE, true)
            startActivity(intent)
        }
        setupMenu(binding.menuFaq, R.drawable.ic_help, "FAQ / Pertanyaan Umum") {
            startActivity(Intent(requireContext(), FaqActivity::class.java))
        }
        setupMenu(binding.menuBantuan, R.drawable.ic_explore, "Pusat Bantuan") {
            val intent = Intent(requireContext(), WebPageActivity::class.java)
            intent.putExtra(WebPageActivity.EXTRA_TITLE, "Pusat Bantuan")
            intent.putExtra(WebPageActivity.EXTRA_URL, "https://pcr.ac.id")
            startActivity(intent)
        }
        setupMenu(binding.menuTentang, R.drawable.ic_info_outline, "Tentang EventPro") {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Tentang EventPro")
                .setMessage(
                    "EventPro adalah platform manajemen event & seminar: temukan event, " +
                        "booking, bayar dengan transfer, unggah bukti, dan dapatkan e-tiket QR.\n\nVersi 1.0"
                )
                .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        binding.btnLogout.setOnClickListener { confirmLogout() }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().setLightStatusBar(false)
        val tickets = TicketRepository(requireContext()).getAll()
        binding.tvStatTotal.text = tickets.size.toString()
        binding.tvStatRegistered.text = tickets.count { it.status == TicketStatus.TERDAFTAR }.toString()
        binding.tvStatWaiting.text = tickets.count { it.status.isWaiting }.toString()
    }

    private fun setupMenu(
        item: ItemProfileMenuBinding,
        @DrawableRes icon: Int,
        title: String,
        onClick: () -> Unit
    ) {
        item.imgIcon.setImageResource(icon)
        item.tvTitle.text = title
        item.root.setOnClickListener { onClick() }
    }

    private fun confirmLogout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Keluar")
            .setMessage("Apakah Anda yakin ingin keluar dari akun ini?")
            .setPositiveButton("Ya") { dialog, _ ->
                session.logout() // hapus data sesi di SharedPreferences
                dialog.dismiss()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
