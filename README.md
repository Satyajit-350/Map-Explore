# Map-Explore

Map Explore is an Android application that allows users to explore nearby locations and view place details. 
This README file provides instructions on how to build and run the app, as well as an overview of its features and design decisions made during development.

## Techs Used 💻
- 100% [Kotlin](https://kotlinlang.org/) based
- [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) 
- [Dagger-Hilt](https://dagger.dev/hilt/) - Standard library to incorporate Dagger dependency injection into an Android application.
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that is lifecycle aware (didn't destroyed on UI changes).
- [Android Architecture Components](https://developer.android.com/topic/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - MVVM Architecture (Declarative View - ViewModel - Model)
  - Repository pattern
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit) - construct the REST APIs and paging network data.
- [Google Maps API](https://developers.google.com/maps/documentation/places/android-sdk/place-details) - Get location data for over 200 million places, and add place details, search, and autocomplete to your apps.

## Features

- Display a map with the user's current location.
- Search for nearby locations based on keywords.
- View details of a specific place.
- Provide location services prompts and settings.

<p align = "center">
 <img alt="GIF" src="https://media.giphy.com/media/XF8JRf1dlKpeecrozH/giphy.gif" width="30%">
 <img alt="GIF" src="https://media.giphy.com/media/lARyCXhGV9fr4gVBA4/giphy.gif" width="30%">
 <img alt="GIF" src="https://media.giphy.com/media/HRVcYLnhOemg3FSkZM/giphy.gif" width="30%">
 </p>
 
 ## Application Install
You can Install and test the app from below 👇

[![Map-Explore](https://img.shields.io/badge/Map_Explore-APK-silver.svg?style=for-the-badge&logo=android)](TODO)

## Build and Run Instructions

1. Clone the repository to your local machine using the following command:
```XML

git clone git@github.com:Satyajit-350/Map-Explore.git

```
2. Open the project in Android Studio.

3. Add the Google Maps API KEY in Manifest file and Constant file
```XML

api_key = "YOUR API KEY"

```
4. Build the project 

5. Run the app on an Android emulator or a physical device by selecting the target device and clicking on the **Run** 

# Design Decisions

- The app uses the Google Maps Android API to display the map and interact with location-related services.
- Location services are checked and enabled/disabled prompts are shown to the user if necessary.
- The app incorporates a search feature to find nearby locations based on user-provided keywords.
- Place details are retrieved using the Google Places API and displayed to the user.
- Bottom sheets are used for filtering and displaying location details.
- The app uses the Model-View-ViewModel (MVVM) architecture for separation of concerns and easier testing.
- Permissions for accessing user location are requested at runtime when necessary.

# Screenshots

<p align = "center">
 <img src = "https://github.com/Satyajit-350/Map-Explore/blob/master/preview/Screenshot_2023-06-13-16-57-49-18_ab1359306de43320f9557c797b1c4be5.jpg" height="350">
 <img src = "https://github.com/Satyajit-350/Map-Explore/blob/master/preview/Screenshot_2023-06-13-17-02-37-79_1e7cdc0e9d37b1b8f62781f4beeab2d1.jpg" height="350">
 <img src = "https://github.com/Satyajit-350/Map-Explore/blob/master/preview/Screenshot_2023-06-13-16-58-04-84_1e7cdc0e9d37b1b8f62781f4beeab2d1.jpg" height="350">
 <img src = "https://github.com/Satyajit-350/Map-Explore/blob/master/preview/Screenshot_2023-06-13-16-58-20-46_1e7cdc0e9d37b1b8f62781f4beeab2d1.jpg" height="350">
 <img src = "https://github.com/Satyajit-350/Map-Explore/blob/master/preview/Screenshot_2023-06-13-16-58-31-15_1e7cdc0e9d37b1b8f62781f4beeab2d1.jpg" height="350">
 <img src = "https://github.com/Satyajit-350/Map-Explore/blob/master/preview/Screenshot_2023-06-13-16-59-19-01_1e7cdc0e9d37b1b8f62781f4beeab2d1.jpg" height="350">
 <img src = "https://github.com/Satyajit-350/Map-Explore/blob/master/preview/Screenshot_2023-06-13-16-59-40-44_1e7cdc0e9d37b1b8f62781f4beeab2d1.jpg" height="350">
 <img src = "https://github.com/Satyajit-350/Map-Explore/blob/master/preview/Screenshot_2023-06-13-17-00-21-28_1e7cdc0e9d37b1b8f62781f4beeab2d1.jpg" height="350">
  
 </p>



