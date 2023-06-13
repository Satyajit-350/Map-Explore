package com.example.assignment.models.nearbySearchRequest

data class PlaceRequest(

    val location: String,
    val radius: Int,
    val type: String,
    val apiKey: String

)
