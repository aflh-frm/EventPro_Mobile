package com.example.eventpro_mobile.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eventpro_mobile.R

/** Tutorial Screen halaman 1 (materi P9: Tutorial1Fragment, Tutorial2Fragment, Tutorial3Fragment). */
class Onboarding1Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding1, container, false)
    }
}
