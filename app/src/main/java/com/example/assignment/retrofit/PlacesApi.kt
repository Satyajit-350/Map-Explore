package com.example.assignment.retrofit

import com.example.assignment.models.autoCompleteResponse.PlacesAutoCompleteResponse
import com.example.assignment.models.nearbySearchResponse.PlacesResponse
import com.example.assignment.models.placeDetails.PlaceDetailsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {

    @GET("nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String,
        @Query("key") apiKey: String
    ): Response<PlacesResponse>

    @GET("autocomplete/json")
    suspend fun getAutoCompletePlaces(
        @Query("input") input: String?,
        @Query("location") location: String,
        @Query("components") component: String,
        @Query("radius") radius: String,
        @Query("key") apiKey: String
    ): Response<PlacesAutoCompleteResponse>

    @GET("details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeID: String?,
        @Query("key") key: String
    ): Response<PlaceDetailsResponse>



}