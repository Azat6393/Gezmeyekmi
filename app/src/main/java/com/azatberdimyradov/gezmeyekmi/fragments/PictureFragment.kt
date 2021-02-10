package com.azatberdimyradov.gezmeyekmi.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.azatberdimyradov.gezmeyekmi.R
import com.azatberdimyradov.gezmeyekmi.databinding.FragmentPictureBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PictureFragment: Fragment(R.layout.fragment_picture) {

    private val args by navArgs<PictureFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPictureBinding.bind(view)

        binding.apply {
            val bitmap = BitmapFactory.decodeByteArray(args.image.picture,0,args.image.picture!!.size)
            fragmentPictureImageView.setImageBitmap(bitmap)
        }
    }
}