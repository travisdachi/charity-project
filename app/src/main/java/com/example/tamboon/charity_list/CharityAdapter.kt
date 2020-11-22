package com.example.tamboon.charity_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tamboon.databinding.ItemCharityBinding
import com.example.tamboon.shared.Charity

class CharityAdapter(
    private val onClickListener: ((Charity) -> Unit)? = null
) : RecyclerView.Adapter<CharityViewHolder>() {
    var charities: List<Charity> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharityViewHolder {
        return CharityViewHolder(ItemCharityBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int = charities.size

    override fun onBindViewHolder(holder: CharityViewHolder, position: Int) {
        val charity = charities[position]
        Glide.with(holder.itemView)
            .load(charity.logo_url)
            .into(holder.binding.imageView)
        holder.binding.textView.text = charity.name
        holder.binding.root.setOnClickListener { onClickListener?.invoke(charity) }
    }
}

class CharityViewHolder(val binding: ItemCharityBinding) : RecyclerView.ViewHolder(binding.root)