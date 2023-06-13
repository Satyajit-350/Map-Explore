package com.example.assignment.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.assignment.R
import com.example.assignment.databinding.ActivityMainBinding
import com.example.assignment.ui.bottomsheet.BottomSheetFilter
import com.example.assignment.ui.bottomsheet.BottomSheetLocation
import com.example.assignment.ui.search.SearchActivity
import com.example.assignment.utils.BottomSheetListener
import com.example.assignment.utils.NetworkResults
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback, BottomSheetListener {

    private var _binding : ActivityMainBinding ?= null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private var googleMap: GoogleMap? = null
    private lateinit var locationManager: LocationManager
    private var locationServicesDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isLocationEnabled) {
            openDialog()
        }

        binding.mapView.onCreate(savedInstanceState)
        MapsInitializer.initialize(this)
        binding.mapView.getMapAsync(this)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //for place details
        viewModel.placeDetailsResponseLiveData.observe(this, Observer {
            when(it){
                is NetworkResults.Error -> {}
                is NetworkResults.Loading -> {}
                is NetworkResults.Success -> {
                    if (it.data != null) {
                        val location = it.data.result.geometry.location
                        animateCamera(location.lat, location.lng, it.data.result.name, it.data.result.place_id)
                    }
                }
            }
        })

        //for nearby place
        viewModel.nearbyResultLiveData.observe(this, Observer {
            when(it){
                is NetworkResults.Error -> {
                    Toast.makeText(this,"Unable to fetch nearby locations", Toast.LENGTH_SHORT).show()
                }
                is NetworkResults.Loading -> {

                }
                is NetworkResults.Success -> {
                    googleMap?.clear()
                    for (place in it.data!!) {
                        val latLng = LatLng(place.geometry.location.lat, place.geometry.location.lng)
                        addMarkersOnMap(latLng, place.place_id, place.types[0])
                    }
                }
            }
        })

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivityForResult(intent, 100)
        }

        binding.myLocationBtn.setOnClickListener {
            googleMap?.clear()
            moveCameraToCurrentLocation()
            Toast.makeText(this,"The Blue Marker is your current location",Toast.LENGTH_SHORT).show()
        }

        //filters
        binding.searchLayout.filterMenu.setOnClickListener {
            val bottomSheet  = BottomSheetFilter()
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val bundle = Bundle().apply {
                            putString("lat_lng", "${location.latitude},${location.longitude}")
                        }
                        bottomSheet.arguments = bundle
                        bottomSheet.show(supportFragmentManager,null)

                    }
                }
            } else {
                val snackbar = Snackbar.make(binding.root, "Location services are disabled. Enable them in settings.", Snackbar.LENGTH_LONG)
                snackbar.setAction("Settings") {
                    // Open the settings screen to enable location services
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
                snackbar.show()
            }
        }

        binding.searchLayout.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                if (ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            googleMap?.clear()
                            viewModel.getNearbyLocations("${location.latitude},${location.longitude}",query.lowercase())
                            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(binding.searchLayout.searchView.windowToken, 0)
                        }
                    }
                } else {
                    val snackbar = Snackbar.make(binding.root, "Location services are disabled. Enable them in settings.", Snackbar.LENGTH_LONG)
                    snackbar.setAction("Settings") {
                        // Open the settings screen to enable location services
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }
                    snackbar.show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })

    }

    private fun openDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Location Services Disabled")
        alertDialogBuilder.setMessage("Please enable location services to use this app.")
        alertDialogBuilder.setPositiveButton("Enable") { dialog, which ->
            // Open the device settings to enable location services
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
            locationServicesDialog!!.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        alertDialogBuilder.setCancelable(true)
        locationServicesDialog = alertDialogBuilder.create()
        locationServicesDialog!!.show()
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Location has changed
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            // Location provider status has changed
        }

        override fun onProviderEnabled(provider: String) {
            // Location provider has been enabled
            moveCameraToCurrentLocation()
            locationServicesDialog?.dismiss()
        }

        override fun onProviderDisabled(provider: String) {
            openDialog()
        }
    }

    private fun addMarkersOnMap(
        latLng: LatLng,
        place_id: String,
        category: String
    ) {
        val marker = googleMap?.addMarker(MarkerOptions().position(latLng).title(place_id))
        marker?.tag = "custom_marker"

        // Set custom marker icon based on the place category
        val markerIcon: BitmapDescriptor = when (category) {
            "hospital" -> BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital)
            "park" -> BitmapDescriptorFactory.fromResource(R.drawable.ic_park)
            "restaurant" -> BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant)
            // Add more categories and corresponding marker icons as needed
            else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) // Default marker icon
        }


        googleMap?.setOnMarkerClickListener { clickedMarker ->
            if (clickedMarker.tag == "custom_marker") {
                val bottomSheet = BottomSheetLocation()
                val bundle = Bundle().apply {
                    putString("key", clickedMarker.title)
                }
                bottomSheet.arguments = bundle
                bottomSheet.show(supportFragmentManager, null)
            }

            true
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val placeId = data?.getStringExtra("placeId")
            // Display the location using the placeId
            if (placeId != null) {
                // Perform actions with the placeId
                viewModel.getPlacesDetails(placeId)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.apply {
            googleMap?.clear()
            uiSettings.isMapToolbarEnabled = false
            uiSettings.isMyLocationButtonEnabled = false
            uiSettings.isCompassEnabled = false
        }

        moveCameraToCurrentLocation()



    }

    private fun moveCameraToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {

                    //display the nearby locations
                    viewModel.getNearbyLocations("${location.latitude},${location.longitude}","")

                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentLatLng,
                            15f
                        )
                    )
                    val markerOption =
                        MarkerOptions().apply {
                             position(currentLatLng)
                            .title("I am here")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                        }
                    googleMap?.addMarker(markerOption)
                }
            }
        } else {
            // Request location permission if it is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, move the camera to the current location
                moveCameraToCurrentLocation()
            } else {
                // Location permission denied, handle accordingly
                // You can show a message or disable location-related functionality
            }
        }
    }

    private fun animateCamera(lat: Double?, lng: Double?, placeName: String = "", place_id: String) {
        googleMap?.clear()
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    lat!!,
                    lng!!
                ), 15f
            )
        )
        val markerOption =
            MarkerOptions().apply {
                title(placeName)
                position(LatLng(lat!!, lng!!))
            }
        val marker = googleMap?.addMarker(markerOption)
        marker?.tag = "custom_marker"

        googleMap?.setOnMarkerClickListener { clickedMarker ->
            if (clickedMarker.tag == "custom_marker") {
                val bottomSheet  = BottomSheetLocation()
                val bundle = Bundle().apply {
                    putString("key", place_id)
                }
                bottomSheet.arguments = bundle
                bottomSheet.show(supportFragmentManager,null)
            }

            true
        }

    }

    override fun onResume() {
        binding.mapView.onResume()
        super.onResume()

        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isLocationEnabled) {
            openDialog()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, register the LocationListener and request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        } else {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
        locationManager.removeUpdates(locationListener)
    }

    override fun onStop() {
        binding.mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        binding.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onMarkerAdd(placeLatLng: LatLng, place_id: String, category: String) {
        addMarkersOnMap(placeLatLng,place_id, category)
    }

    override fun clearMarkers() {
        googleMap?.clear()
    }

}