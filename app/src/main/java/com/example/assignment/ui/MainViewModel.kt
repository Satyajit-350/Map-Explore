package com.example.assignment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.models.nearbySearchRequest.PlaceRequest
import com.example.assignment.models.placeDetialsRequest.PlaceDetailsRequest
import com.example.assignment.retrofit.repository.PlacesRepository
import com.example.assignment.utils.Constants
import com.example.assignment.utils.Constants.API_KEY
import com.example.assignment.utils.Constants.NEARBY_RADIUS
import com.example.assignment.utils.Constants.PLACE_AUTOCOMPLETE_RADIUS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val placesRepository: PlacesRepository): ViewModel() {

    val placeDetailsResponseLiveData get() = placesRepository.placeDetailsResponse

    val nearbyResultLiveData get() = placesRepository.nearbyResultLiveData

    fun getPlacesDetails(id: String) {
        val placeDetailsRequest = PlaceDetailsRequest(id,
            API_KEY
        )
        viewModelScope.launch {
            placesRepository.getPlaceDetails(placeDetailsRequest)
        }
    }

    fun getNearbyLocations(location: String,type: String){
        val placeRequest = PlaceRequest(location, NEARBY_RADIUS, type, API_KEY)
        viewModelScope.launch {
            placesRepository.getNearbySearchResults(placeRequest)
        }
    }

}