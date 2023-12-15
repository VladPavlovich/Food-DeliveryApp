package com.text.finalproject

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.text.finalproject.databinding.FragmentHomeScreenBinding
import kotlinx.coroutines.launch
import kotlin.math.log

class HomeScreen : Fragment() {

    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!
    val viewModel : SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        val view = binding.root
//clears the restaurant details
        viewModel.clearRestaurantDetails()

//        binding.homeScreenButton.setOnClickListener {
//                uploadRestaurantData()
//        }




        return view
    }






   // uploaded data to firebase for each firebase restaurant
//    private fun uploadRestaurantData() {
//        val uploader = FirebaseDataUploader()
//        lifecycleScope.launch {
//            try {
//                uploader.uploadData()
//                Log.d("HomeScreen", "Data uploaded successfully")
//            } catch (e: Exception) {
//                Log.e("HomeScreen", "Error uploading data", e)
//            }
//        }
//    }


}