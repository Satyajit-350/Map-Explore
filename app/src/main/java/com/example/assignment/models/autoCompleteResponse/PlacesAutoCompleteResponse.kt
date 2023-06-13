package com.example.assignment.models.autoCompleteResponse

data class PlacesAutoCompleteResponse(
    val predictions: List<Prediction>,
    val status: String
)