package com.azatberdimyradov.gezmeyekmi.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.azatberdimyradov.gezmeyekmi.R
import com.azatberdimyradov.gezmeyekmi.adapters.DetailsPicturesAdapter
import com.azatberdimyradov.gezmeyekmi.data.CityPhoto
import com.azatberdimyradov.gezmeyekmi.databinding.FragmentDetailsBinding
import com.azatberdimyradov.gezmeyekmi.viewmodel.DetailsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.ByteArrayOutputStream
import java.lang.Exception

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details),
    DetailsPicturesAdapter.OnItemClickListener {

    private val args by navArgs<DetailsFragmentArgs>()
    private val viewModel: DetailsViewModel by viewModels()

    var selectedPicture: Uri? = null
    var selectedBitmap: Bitmap? = null
    lateinit var picturesAdapter: DetailsPicturesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDetailsBinding.bind(view)
        picturesAdapter = DetailsPicturesAdapter(this)

        binding.apply {
            fragmentDetailsNoteEditText.clearFocus()
            fragmentDetailsNoteEditText.setText(args.city.note)

            fragmentDetailsRecyclerView.apply {
                adapter = picturesAdapter
                layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                setHasFixedSize(true)
            }

            fragmentDetailsNoteEditText.addTextChangedListener {
                viewModel.update(args.city, it.toString())
            }

            fragmentDetailsPlusButton.setOnClickListener {
                checkPermission()
            }
        }
        viewModel.getPictures(args.name).observe(viewLifecycleOwner) {
            picturesAdapter.notifyDataSetChanged()
            picturesAdapter.submitList(it)
        }
    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intentToGallery, 2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentToGallery, 2)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {

            selectedPicture = data.data

            try {
                if (selectedPicture != null) {
                    selectedBitmap = if (Build.VERSION.SDK_INT >= 28) {
                        val source =
                            ImageDecoder.createSource(
                                requireActivity().contentResolver,
                                selectedPicture!!
                            )
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        MediaStore.Images.Media.getBitmap(
                            requireActivity().contentResolver,
                            selectedPicture
                        )
                    }
                    if (selectedBitmap != null) {
                        val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 500)
                        val outputStream = ByteArrayOutputStream()
                        smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
                        val byteArray = outputStream.toByteArray()
                        viewModel.insertPicture(args.name, byteArray)
                    }
                }
            } catch (e: Exception) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun makeSmallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio: Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1) {
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    override fun onItemClick(cityPhoto: CityPhoto) {
        val action = DetailsFragmentDirections.actionDetailsFragmentToPictureFragment(cityPhoto)
        findNavController().navigate(action)
    }

    override fun onDeleteClick(pictures: List<CityPhoto>) {
        viewModel.deletePictures(pictures)
    }
}