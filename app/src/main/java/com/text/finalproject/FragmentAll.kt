package com.text.finalproject

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.text.finalproject.databinding.FragmentAllBinding

class FragmentAll : Fragment() {

    private var _binding: FragmentAllBinding? = null
    private val binding get() = _binding!!
    private lateinit var restaurantAdapter: RestaurantAdapter
    val viewModel : SharedViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAllBinding.inflate(inflater, container, false)
        setupRecyclerView()
        loadRestaurants()
        return binding.root
    }
//set up recycler view
    private fun setupRecyclerView() {
        restaurantAdapter = RestaurantAdapter { restaurant ->
            // Handle restaurant click
            Log.i("AllRestaurantsFragment", "Clicked on restaurant: ${restaurant.name}")
            navigateToRestaurantScreen(restaurant)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = restaurantAdapter
        }
    }
//load restaurants
    private fun loadRestaurants() {
        val db = FirebaseFirestore.getInstance()
        db.collection("restaurants")
            .get()
            .addOnSuccessListener { documents ->
                val restaurantList = documents.map { document ->
                    document.toObject(Restaurant::class.java)
                }
                restaurantAdapter.submitList(restaurantList)
            }
            .addOnFailureListener { e ->
                Log.w("AllRestaurantsFragment", "Error loading restaurants", e)
            }
    }
//navigate to restaurant screen
    private fun navigateToRestaurantScreen(restaurant: Restaurant) {
        Log.i("AllRestaurantsFragment", restaurant.foodItems.toString())
        val  location = restaurant.location
        val itemsArray = restaurant.foodItems.toTypedArray()
       Log.i("AllRestaurantsFragment", "Restaurant location: ${restaurant.location.toString()}")

        viewModel.saveRestaurantDetails(restaurant.name, restaurant.location)
        val action = HomeScreenDirections.actionHomeScreenToRestaurantScreen(restaurant.name, itemsArray)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
