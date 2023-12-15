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
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.text.finalproject.databinding.FragmentFavoriteBinding

class FragmentFavorite : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var restaurantAdapter: RestaurantAdapter
    val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        setupRecyclerView()
        loadRestaurants()  // Assuming you want to load similar data
        return binding.root
    }
//setup recycler view
    private fun setupRecyclerView() {
        restaurantAdapter = RestaurantAdapter { restaurant ->
            // Handle restaurant click
            navigateToRestaurantScreen(restaurant)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = restaurantAdapter
        }
    }
//database retrieval
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
