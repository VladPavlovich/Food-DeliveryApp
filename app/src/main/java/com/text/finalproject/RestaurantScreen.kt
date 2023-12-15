package com.text.finalproject

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.text.finalproject.databinding.FragmentRestaurantScreenBinding

class RestaurantScreen : Fragment() {

    private lateinit var binding: FragmentRestaurantScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRestaurantScreenBinding.inflate(inflater, container, false)

        val restaurantName = arguments?.getString("restaurantName")
        val foodItems = arguments?.getParcelableArray("foodItems")?.map { it as FoodItem }
//        val location = arguments?.getParcelable<GeoPoint>("location")




        Log.i("RestaurantScreen", "Restaurant name: $restaurantName")
        Log.i("RestaurantScreen", "Restaurant items: $foodItems")

        if (restaurantName != null) {
            fetchImageUrls(restaurantName) { imageUrls ->
                setupImagesFragment(imageUrls)
            }

            // Set up the food items fragment
            if (foodItems != null) {
                setupFoodItemsFragment(foodItems)
            }


            setupRestaurantName(restaurantName)
        }

        return binding.root
    }
//fetches the image urls
    private fun fetchImageUrls(restaurantName: String, callback: (List<String>) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val restaurantImagesRef = storageRef.child("restaurantImages/$restaurantName")

        restaurantImagesRef.listAll()
            .addOnSuccessListener { listResult ->
                val urlTasks = listResult.items.map { it.downloadUrl }
                Tasks.whenAllSuccess<Uri>(urlTasks)
                    .addOnSuccessListener { urlList ->
                        val imageUrls = urlList.map { it.toString() }
                        callback(imageUrls)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("RestaurantScreen", "Error fetching image URLs", exception)
            }
    }
//sets up the images fragment
    private fun setupImagesFragment(imageUrls: List<String>) {
        val imagesFragment = RestaurantImagesFragment().apply {
            arguments = Bundle().apply {
                putStringArrayList("imageUrls", ArrayList(imageUrls))
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.restaurantImagesFragment, imagesFragment)
            .commit()
    }

    private fun setupFoodItemsFragment(foodItems: List<FoodItem>) {
        val foodItemsWithDefaultQuantities = foodItems.map { Pair(it, 1) } // Assign a default quantity of 1

        val foodItemsFragment = RestaurantFoodItemsFragment().apply {
            setFoodItems(foodItemsWithDefaultQuantities)
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.restaurantFoodItemsFragment, foodItemsFragment)
            .commit()
    }

    private fun setupRestaurantName(restaurantName: String) {
        val foodItemsFragment = childFragmentManager.findFragmentById(R.id.restaurantFoodItemsFragment) as? RestaurantFoodItemsFragment
        foodItemsFragment?.setRestaurantName(restaurantName)

    }
}
