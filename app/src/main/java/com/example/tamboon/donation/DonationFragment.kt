package com.example.tamboon.donation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tamboon.R
import com.example.tamboon.databinding.FragmentDonationBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DonationFragment : Fragment() {

    private val viewModel: DonationViewModel by viewModel()
    private lateinit var binding: FragmentDonationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDonationBinding.inflate(layoutInflater, container, false)
        binding.run {
            submitButton.setOnClickListener {
                lifecycleScope.launch {
                    val result = viewModel.submitDonation(
                        nameEditText.text.toString(),
                        amountEditText.text.toString().toInt(),
                        creditCardEditText.text.toString(),
                        expireEditText.expiryMonth,
                        expireEditText.expiryYear,
                        cvvEditText.securityCode
                    )
                    when (result) {
                        DonationResult.Success -> findNavController().navigate(R.id.action_donationFragment_to_successFragment)
                        is DonationResult.Failure -> Snackbar.make(requireView(), result.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            creditCardEditText.addTextChangedListener { validate() }
            nameEditText.addTextChangedListener { validate() }
            expireEditText.addTextChangedListener { validate() }
            cvvEditText.addTextChangedListener { validate() }
            amountEditText.addTextChangedListener { validate() }
        }
        return binding.root
    }

    private fun FragmentDonationBinding.validate() {
        viewModel.validate(
            creditCardEditText.cardNumber,
            nameEditText.cardName,
            expireEditText.expiryMonth,
            expireEditText.expiryYear,
            cvvEditText.securityCode,
            amountEditText.text.toString().toIntOrNull() ?: 0
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            binding.run {
                submitButton.isEnabled = state.isValid && !state.isLoading
                creditCardEditText.isEnabled = !state.isLoading
                nameEditText.isEnabled = !state.isLoading
                expireEditText.isEnabled = !state.isLoading
                cvvEditText.isEnabled = !state.isLoading
                amountEditText.isEnabled = !state.isLoading
                progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            }
        })
    }

}