package com.example.eventpro_mobile.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.eventpro_mobile.databinding.FragmentOnboardingBinding

/**
 * Satu halaman onboarding. Isi dikirim lewat arguments supaya satu Fragment
 * bisa dipakai untuk 3 halaman (lebih ringkas dari Tutorial1/2/3Fragment).
 */
class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = requireArguments()
        binding.imgIcon.setImageResource(args.getInt(ARG_ICON))
        binding.tvTag.text = args.getString(ARG_TAG)
        binding.tvTitle.text = args.getString(ARG_TITLE)
        binding.tvDesc.text = args.getString(ARG_DESC)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ICON = "icon"
        private const val ARG_TAG = "tag"
        private const val ARG_TITLE = "title"
        private const val ARG_DESC = "desc"

        fun newInstance(@DrawableRes icon: Int, tag: String, title: String, desc: String) =
            OnboardingFragment().apply {
                arguments = bundleOf(
                    ARG_ICON to icon,
                    ARG_TAG to tag,
                    ARG_TITLE to title,
                    ARG_DESC to desc
                )
            }
    }
}
