package com.azatberdimyradov.gezmeyekmi.fragments

import android.content.Context
import android.content.res.Resources
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.azatberdimyradov.gezmeyekmi.R
import com.azatberdimyradov.gezmeyekmi.data.City
import com.azatberdimyradov.gezmeyekmi.viewmodel.CitiesListViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MapsFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    //private lateinit var locationListener: LocationListener

    private lateinit var citiesList: List<City>

    private val viewModel: CitiesListViewModel by viewModels()

    private val callback = OnMapReadyCallback { googleMap ->
        try {

            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context, R.raw.mapstyle
                )
            )

            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsActivity", "Can't find style. Error: ", e)
        }

        mMap = googleMap
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val turkey = LatLng(39.214096, 35.310965)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(turkey, 6.8f))
        mMap.setMaxZoomPreference(9f)
        mMap.setMinZoomPreference(6f)
        mMap.setOnMapClickListener(myListener)

        viewModel.cities.observe(viewLifecycleOwner) {
            citiesList = it
            for (city in it) {
                val latLog = LatLng(city.latitude, city.longitude)
                mMap.addMarker(
                    MarkerOptions()
                        .title(city.createDateFormatted)
                        .position(latLog)
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    val myListener = object : GoogleMap.OnMapClickListener {
        override fun onMapClick(p0: LatLng?) {

            val geoCoder = Geocoder(context, Locale.getDefault())

            if (p0 != null) {
                try {
                    val cityList = geoCoder.getFromLocation(p0.latitude, p0.longitude, 1)
                    if (cityList != null && cityList.size > 0) {

                        if (cityList[0].adminArea != null) {

                            var found = false
                            for (it in citiesList) {
                                if (it.name.equals(cityList[0].adminArea)) {
                                    found = true
                                }
                            }
                            if (found) {
                                Toast.makeText(
                                    context,
                                    "You already have been there",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val city = City(
                                    name = cityList[0].adminArea,
                                    latitude = cityList[0].latitude,
                                    longitude = cityList[0].longitude,
                                    note = ""
                                )
                                viewModel.onAddCity(city)

                                Toast.makeText(context, cityList[0].adminArea, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                } catch (e: Exception) {

                }
            }
        }
    }
}