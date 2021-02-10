package com.azatberdimyradov.gezmeyekmi.adapters

import android.graphics.BitmapFactory
import android.view.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.azatberdimyradov.gezmeyekmi.R
import com.azatberdimyradov.gezmeyekmi.data.CityPhoto
import com.azatberdimyradov.gezmeyekmi.databinding.RecyclerViewDetailsFragmentPicturesItemBinding

class DetailsPicturesAdapter(private val listener: OnItemClickListener) :
    ListAdapter<CityPhoto, DetailsPicturesAdapter.PicturesViewHolder>(DiffCallBack()) {

    var isEnable = false
    var isAllSelected = false
    var selectedList = arrayListOf<CityPhoto>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailsPicturesAdapter.PicturesViewHolder {
        val binding = RecyclerViewDetailsFragmentPicturesItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PicturesViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DetailsPicturesAdapter.PicturesViewHolder,
        position: Int
    ) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class PicturesViewHolder(private val binding: RecyclerViewDetailsFragmentPicturesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val picture = getItem(position)
                        listener.onItemClick(picture)
                    }
                }
                root.setOnLongClickListener {
                    if (!isEnable) {
                        isEnable = true
                        notifyDataSetChanged()
                        var callBack = object : ActionMode.Callback {
                            override fun onCreateActionMode(
                                mode: ActionMode?,
                                menu: Menu?
                            ): Boolean {
                                val inflater = mode?.menuInflater
                                inflater?.inflate(R.menu.delete_menu, menu)
                                return true
                            }

                            override fun onPrepareActionMode(
                                mode: ActionMode?,
                                menu: Menu?
                            ): Boolean {
                                return true
                            }

                            override fun onActionItemClicked(
                                mode: ActionMode?,
                                item: MenuItem?
                            ): Boolean {
                                when (item?.itemId) {
                                    R.id.delete_item -> {
                                        listener.onDeleteClick(selectedList)
                                        mode?.finish()
                                    }
                                    R.id.select_all_item -> {
                                        isAllSelected = true
                                        selectedList.clear()
                                        notifyDataSetChanged()
                                    }
                                }
                                return true
                            }

                            override fun onDestroyActionMode(mode: ActionMode?) {
                                isEnable = false
                                isAllSelected = false
                                selectedList.clear()
                                notifyDataSetChanged()
                            }
                        }
                        it.startActionMode(callBack)
                    }
                    true
                }
                recyclerViewPicturesCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val picture = getItem(position)
                            selectedList.add(picture)
                            println(selectedList.size)
                        }

                    } else {
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val picture = getItem(position)
                            selectedList.remove(picture)
                        }
                    }
                }
            }
        }

        fun bind(cityPhoto: CityPhoto) {
            binding.apply {
                if (cityPhoto.picture != null) {
                    val bitmap =
                        BitmapFactory.decodeByteArray(cityPhoto.picture, 0, cityPhoto.picture.size)
                    recyclerViewPicturesImageView.setImageBitmap(bitmap)
                    recyclerViewPicturesCheckBox.isVisible = isEnable
                    recyclerViewPicturesCheckBox.isChecked = isAllSelected
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(cityPhoto: CityPhoto)
        fun onDeleteClick(pictures: List<CityPhoto>)
    }

    class DiffCallBack : DiffUtil.ItemCallback<CityPhoto>() {
        override fun areItemsTheSame(oldItem: CityPhoto, newItem: CityPhoto) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CityPhoto, newItem: CityPhoto) =
            oldItem == newItem
    }
}

