package com.example.assignment.models.autoCompleteRequest

data class PlaceAutoCompleteRequest(

    val input: String,
    val location: String,
    val component: String,
    val radius: String,
    val apiKey: String

)
