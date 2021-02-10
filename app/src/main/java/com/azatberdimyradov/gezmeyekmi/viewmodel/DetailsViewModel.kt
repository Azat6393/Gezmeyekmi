package com.azatberdimyradov.gezmeyekmi.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.azatberdimyradov.gezmeyekmi.data.City
import com.azatberdimyradov.gezmeyekmi.data.CityDao
import com.azatberdimyradov.gezmeyekmi.data.CityPhoto
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DetailsViewModel @ViewModelInject constructor(
    val cityDao: CityDao
) : ViewModel() {

    fun getPictures(name: String): LiveData<List<CityPhoto>> {
        return cityDao.getCityPicture(name).asLiveData()
    }

    val allPictures = cityDao.getAllCityPictures().asLiveData()

    fun insertPicture(name: String, byteArray: ByteArray) = viewModelScope.launch {
        cityDao.insertPicture(CityPhoto(name,byteArray))
    }

    fun update(city: City, newNote: String) = viewModelScope.launch {
        val newCity = city.copy(note = newNote)
        cityDao.update(newCity)
    }

    fun deletePictures(pictures: List<CityPhoto>) = viewModelScope.launch {
        pictures.forEach {
            launch {
                cityDao.deletePicture(it)
            }
        }
    }
}