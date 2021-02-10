package com.azatberdimyradov.gezmeyekmi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.azatberdimyradov.gezmeyekmi.data.City
import androidx.recyclerview.widget.DiffUtil
import com.azatberdimyradov.gezmeyekmi.databinding.RecyclerViewCitiesListBinding

class CitiesListAdapter(private val listener: OnItemClickListener): ListAdapter<City, CitiesListAdapter.CityViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CitiesListAdapter.CityViewHolder {
        val binding = RecyclerViewCitiesListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitiesListAdapter.CityViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class CityViewHolder(private val binding: RecyclerViewCitiesListBinding): RecyclerView.ViewHolder(binding.root){

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val city = getItem(position)
                        listener.onItemClick(city)
                    }
                }
            }
        }
        fun bind(city: City){
            binding.apply {
                recyclerViewCityName.text = city.name
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(city: City)
    }

    class DiffCallback : DiffUtil.ItemCallback<City>(){
        override fun areItemsTheSame(oldItem: City, newItem: City) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: City, newItem: City) =
            oldItem == newItem
    }
}