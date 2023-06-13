package com.example.assignment.ui.bottomsheet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.assignment.R
import com.example.assignment.adapters.ImageAdapter
import com.example.assignment.databinding.BottomSheetDetailsBinding
import com.example.assignment.ui.MainViewModel
import com.example.assignment.utils.NetworkResults
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetLocation : BottomSheetDialogFragment() {

    private var _binding : BottomSheetDetailsBinding?=null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: ImageAdapter
    private lateinit var placeId: String
    private var latitude: Double?=null
    private var longitude: Double?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetDetailsBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        adapter = ImageAdapter()
        placeId = arguments?.getString("key").toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imagesRv.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
//        binding.imagesRv.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

        binding.imagesRv.adapter = adapter

        // Customize your BottomSheet view and handle interactions here
        viewModel.placeDetailsResponseLiveData.observe(viewLifecycleOwner, Observer {
            when(it){
                is NetworkResults.Error -> {}
                is NetworkResults.Loading -> {}
                is NetworkResults.Success -> {
                    if (it.data != null) {
                        adapter.submitList(it.data.result.photos)
                        latitude = it.data.result.geometry.location.lat
                        longitude = it.data.result.geometry.location.lng
                        binding.locationAddress.text = it.data.result.formatted_address
                        binding.locationName.text = it.data.result.name
                        Picasso.get().load(it.data.result.icon).into(binding.iconImage)
                        try{
                            val openOrClose = it.data.result.current_opening_hours.open_now
                            if(openOrClose){
                                binding.openClose.text = "Open"
                                binding.openClose.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                            }else{
                                binding.openClose.text = "Close"
                                binding.openClose.setTextColor(ContextCompat.getColor(requireContext(), androidx.appcompat.R.color.error_color_material_dark))
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                            binding.openClose.visibility = View.GONE
                        }

                        binding.rating.text = it.data.result.rating.toString()
                        binding.ratingBar.rating = it.data.result.rating.toFloat() - 0.1f
                        binding.ratingBar.numStars = 5
                        binding.ratingBar.stepSize = 0.1f

                        binding.totalRating.text = "( ${it.data.result.user_ratings_total.toString()} )"

                    }
                }
            }
        })

        viewModel.getPlacesDetails(placeId)

        binding.direction.setOnClickListener {
            if(latitude!=null && longitude!=null){
                navigateToLocation(latitude!!,longitude!!)
            }
        }

    }

    private fun navigateToLocation(latitude: Double, longitude: Double) {
        val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps") // Using Google Maps app

        val packageManager = requireActivity().packageManager
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            Toast.makeText(requireContext(), "Google Maps is not installed.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}