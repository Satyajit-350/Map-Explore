package com.example.assignment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.databinding.ItemLocationBinding
import com.example.assignment.models.autoCompleteResponse.Prediction

class SearchPlaceAdapter(private val onLocationClicked: (Prediction) -> Unit) :
    ListAdapter<Prediction, SearchPlaceAdapter.SearchPlaceViewHolder>(ComparatorDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPlaceViewHolder {
        val binding = ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchPlaceViewHolder(binding)
    }


    override fun onBindViewHolder(holder: SearchPlaceViewHolder, position: Int) {
        val item = getItem(position)
        item.let {
            holder.bind(it)
        }
    }

    class ComparatorDiffUtil : DiffUtil.ItemCallback<Prediction>() {
        override fun areItemsTheSame(oldItem: Prediction, newItem: Prediction): Boolean {
            return oldItem.place_id == newItem.place_id
        }

        override fun areContentsTheSame(oldItem: Prediction, newItem: Prediction): Boolean {
            return oldItem == newItem
        }
    }

    inner class SearchPlaceViewHolder(private val binding: ItemLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Prediction) {
            binding.locationDesc.text = item.description
            binding.locationDetails.text = item.structured_formatting.main_text
            binding.cardView.setOnClickListener {
                onLocationClicked(item)
            }
        }
    }
}