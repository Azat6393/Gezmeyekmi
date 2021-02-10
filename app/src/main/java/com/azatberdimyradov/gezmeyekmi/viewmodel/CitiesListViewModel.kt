package com.azatberdimyradov.gezmeyekmi.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.azatberdimyradov.gezmeyekmi.data.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class CitiesListViewModel @ViewModelInject constructor(
     private val cityDao: CityDao,
     private val preferencesManager: PreferenceManager,
     @Assisted private val state: SavedStateHandle
): ViewModel() {

    val searchQuery = state.getLiveData("searchQuery","")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val citiesEventChannel = Channel<CitiesEvent>()
    val citiesEvent = citiesEventChannel.receiveAsFlow()

    private val citiesFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ){ query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        cityDao.getCitiesWithFilter(query, filterPreferences.sortOrder)
    }

    val cities = citiesFlow.asLiveData()

    fun onAddCity(city: City) = viewModelScope.launch {
        cityDao.insert(city)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSordOrder(sortOrder)
    }

    fun onCitySwipe(city: City) = viewModelScope.launch {
        val list = cityDao.getDeletedPictures(city.name)
        cityDao.delete(city)
        cityDao.deletePictures(city.name)
        citiesEventChannel.send(CitiesEvent.ShowUndoDeleteCityMessage(city, list))
    }

    fun onUndoDeleteClick(city: City, pictures: List<CityPhoto>) = viewModelScope.launch {
        cityDao.insert(city)
        for (picture in pictures){
            cityDao.insertPicture(picture)
        }
    }

    sealed class CitiesEvent{
        data class ShowUndoDeleteCityMessage(val city: City, val pictures: List<CityPhoto>): CitiesEvent()
    }
}