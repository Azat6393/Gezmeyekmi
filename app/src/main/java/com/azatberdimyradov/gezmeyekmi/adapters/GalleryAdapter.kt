package com.azatberdimyradov.gezmeyekmi.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.azatberdimyradov.gezmeyekmi.data.CityPhoto
import com.azatberdimyradov.gezmeyekmi.databinding.RecyclerViewGalleryItemBinding

class GalleryAdapter(private val listener: OnItemClickListener): ListAdapter<CityPhoto, GalleryAdapter.GalleryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = RecyclerViewGalleryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class GalleryViewHolder(private val binding: RecyclerViewGalleryItemBinding): RecyclerView.ViewHolder(binding.root){

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    val photo = getItem(position)
                    listener.onItemClick(photo)
                }
            }
        }

        fun bind(cityPhoto: CityPhoto){
            binding.apply {
                if (cityPhoto.picture != null){
                    val bitmap = BitmapFactory.decodeByteArray(cityPhoto.picture,0,cityPhoto.picture.size)
                    recyclerViewGalleryItemImageView.setImageBitmap(bitmap)
                }
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(cityPhoto: CityPhoto)
    }

    class DiffCallback: DiffUtil.ItemCallback<CityPhoto>(){
        override fun areItemsTheSame(oldItem: CityPhoto, newItem: CityPhoto) =
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: CityPhoto, newItem: CityPhoto) =
            oldItem == newItem
    }
}