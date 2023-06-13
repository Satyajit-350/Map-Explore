package com.example.assignment.ui.search

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment.adapters.SearchPlaceAdapter
import com.example.assignment.databinding.ActivitySearchBinding
import com.example.assignment.models.autoCompleteResponse.Prediction
import com.example.assignment.utils.NetworkResults
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private var _binding : ActivitySearchBinding?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: SearchViewModel

    private lateinit var adapter: SearchPlaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        binding.placesList.layoutManager = LinearLayoutManager(this)

        adapter = SearchPlaceAdapter(::onLocationClicked)

        binding.placesList.adapter = adapter

        viewModel.autocompleteResultLiveData.observe(this, Observer {
            when(it){
                is NetworkResults.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
                is NetworkResults.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is NetworkResults.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.placesList.visibility = View.VISIBLE
                    adapter.submitList(it.data)
                }
            }
        })

        binding.searchPlacesEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 3) {
                    binding.indicator.visibility = View.GONE
                    viewModel.getPlacesFromAutocomplete(s.toString())
                } else {
                    binding.placesList.visibility = View.GONE
                    binding.indicator.visibility = View.VISIBLE
                }
            }
        })

        binding.arrowBack.setOnClickListener {
            finish()
        }
    }

    private fun onLocationClicked(prediction: Prediction){
        val intent = Intent()
        intent.putExtra("placeId", prediction.place_id)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}