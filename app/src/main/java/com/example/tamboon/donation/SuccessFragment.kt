package com.example.tamboon.donation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tamboon.R
import com.example.tamboon.databinding.FragmentSuccessBinding

class SuccessFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSuccessBinding.inflate(layoutInflater, container, false)
        binding.buttonDismiss.setOnClickListener {
            findNavController().navigate(R.id.action_successFragment_to_charityListFragment)
        }
        return binding.root
    }
}