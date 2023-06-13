package com.example.assignment.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.models.autoCompleteRequest.PlaceAutoCompleteRequest
import com.example.assignment.retrofit.repository.PlacesRepository
import com.example.assignment.utils.Constants.API_KEY
import com.example.assignment.utils.Constants.PLACE_AUTOCOMPLETE_COMPONENT
import com.example.assignment.utils.Constants.PLACE_AUTOCOMPLETE_RADIUS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val placesRepository: PlacesRepository): ViewModel() {

    val autocompleteResultLiveData get() = placesRepository.autocompleteResultLiveData

    fun getPlacesFromAutocomplete(input: String) {
        val autoCompleteRequest = PlaceAutoCompleteRequest(input,"",PLACE_AUTOCOMPLETE_COMPONENT,PLACE_AUTOCOMPLETE_RADIUS,
            API_KEY)
        viewModelScope.launch {
            placesRepository.getAutoCompletePlaces(autoCompleteRequest)
        }
    }

}