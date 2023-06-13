package com.example.assignment.ui.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.assignment.R
import com.example.assignment.databinding.BottomSheetFilterBinding
import com.example.assignment.ui.MainViewModel
import com.example.assignment.utils.BottomSheetListener
import com.example.assignment.utils.NetworkResults
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomSheetFilter : BottomSheetDialogFragment() {

    private var _binding : BottomSheetFilterBinding?=null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    private var listener: BottomSheetListener? = null
    private lateinit var placeLatLng: String

    override fun onStart() {
        super.onStart()
        val bottomSheetBehavior = BottomSheetBehavior.from(requireView().parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFilterBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        placeLatLng = arguments?.getString("lat_lng").toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.nearbyResultLiveData.observe(viewLifecycleOwner, Observer {
            listener?.clearMarkers()
            when(it){
                is NetworkResults.Error -> {
                    Toast.makeText(requireContext(),"Unable to fetch nearby locations", Toast.LENGTH_SHORT).show()
                }
                is NetworkResults.Loading ->{}
                is NetworkResults.Success ->{
                    for (place in it.data!!) {
                        val latLng = LatLng(place.geometry.location.lat, place.geometry.location.lng)
                        listener?.onMarkerAdd(latLng,place.place_id, place.types[0])
                    }
                }
            }
        })

        binding.filterRG.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.filterByRestaurants -> {
                    viewModel.getNearbyLocations(placeLatLng,"restaurant")
                }
                R.id.filterByParks -> {
                    viewModel.getNearbyLocations(placeLatLng,"park")
                }
                R.id.filterByAtm -> {
                    viewModel.getNearbyLocations(placeLatLng,"atm")
                }
                R.id.filterByHospitals -> {
                    viewModel.getNearbyLocations(placeLatLng,"hospital")
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomSheetListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement BottomSheetListener")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

}