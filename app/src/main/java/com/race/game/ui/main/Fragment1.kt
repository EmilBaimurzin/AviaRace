package com.race.game.ui.main

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.race.game.R
import com.race.game.databinding.Fragment1Binding
import com.race.game.ui.other.ViewBindingFragment

class Fragment1 : ViewBindingFragment<Fragment1Binding>(Fragment1Binding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.start.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentMain_to_fragmentRace)
        }

        binding.exit.setOnClickListener {
            requireActivity().finish()
        }

        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }
    }
}