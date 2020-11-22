package com.example.tamboon.charity_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamboon.R
import com.example.tamboon.databinding.FragmentCharityListBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CharityListFragment : Fragment() {
    private val charityListViewModel: CharityListViewModel by sharedViewModel()
    private lateinit var binding: FragmentCharityListBinding
    private val charityAdapter = CharityAdapter {
        findNavController().navigate(R.id.action_charityListFragment_to_donationFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharityListBinding.inflate(layoutInflater, container, false)
        binding.recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            adapter = charityAdapter
        }
        binding.errorContainer.setOnClickListener {
            charityListViewModel.getCharities()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        charityListViewModel.charityListState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                CharityListState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.emptyTextView.visibility = View.GONE
                    binding.errorContainer.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                }
                CharityListState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyTextView.visibility = View.VISIBLE
                    binding.errorContainer.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                }
                is CharityListState.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyTextView.visibility = View.GONE
                    binding.errorContainer.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.errorTextView.text = state.message
                }
                is CharityListState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyTextView.visibility = View.GONE
                    binding.errorContainer.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    charityAdapter.charities = state.list
                }
            }
        })
        charityListViewModel.getCharities()
    }

}