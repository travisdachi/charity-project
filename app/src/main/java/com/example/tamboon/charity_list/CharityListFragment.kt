package com.example.tamboon.charity_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tamboon.databinding.FragmentCharityListBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CharityListFragment : Fragment() {
    private val charityListViewModel: CharityListViewModel by sharedViewModel()
    private lateinit var binding: FragmentCharityListBinding
    private lateinit var adapter: RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharityListBinding.inflate(layoutInflater, container, false)
        binding.recyclerView.run {
            layoutManager = LinearLayoutManager(context)
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
                    binding.errorTextView.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                }
                CharityListState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyTextView.visibility = View.VISIBLE
                    binding.errorTextView.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                }
                is CharityListState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyTextView.visibility = View.GONE
                    binding.errorTextView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    binding.errorTextView.text = state.message
                }
                is CharityListState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyTextView.visibility = View.GONE
                    binding.errorTextView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
//                    adapter
                }
            }
        })
        charityListViewModel.getCharities()
    }

}