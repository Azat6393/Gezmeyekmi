package com.azatberdimyradov.gezmeyekmi.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.azatberdimyradov.gezmeyekmi.R
import com.azatberdimyradov.gezmeyekmi.adapters.GalleryAdapter
import com.azatberdimyradov.gezmeyekmi.data.CityPhoto
import com.azatberdimyradov.gezmeyekmi.databinding.FragmentGalleryBinding
import com.azatberdimyradov.gezmeyekmi.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment: Fragment(R.layout.fragment_gallery), GalleryAdapter.OnItemClickListener {

    private val viewModel: DetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentGalleryBinding.bind(view)
        val galleryAdapter = GalleryAdapter(this)

        binding.apply {
            fragmentGalleryRecyclerView.apply {
                adapter = galleryAdapter
                layoutManager = GridLayoutManager(requireContext(),3)
                setHasFixedSize(true)
            }
        }
        viewModel.allPictures.observe(viewLifecycleOwner){
            galleryAdapter.submitList(it)
        }
    }

    override fun onItemClick(cityPhoto: CityPhoto) {
        val action = GalleryFragmentDirections.actionGalleryFragmentToPictureFragment(cityPhoto)
        findNavController().navigate(action)
    }
}