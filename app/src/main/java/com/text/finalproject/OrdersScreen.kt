package com.text.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.text.finalproject.databinding.FragmentOrdersScreenBinding

class OrdersScreen : Fragment() {

    private var _binding: FragmentOrdersScreenBinding? = null
    private val binding get() = _binding!!
    val viewModel : SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersScreenBinding.inflate(inflater, container, false)
        loadMostRecentOrder()



        return binding.root
    }
//loads the most recent order
    private fun loadMostRecentOrder() {

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("CheckoutScreen", "User is not logged in.")
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).collection("orders")            .orderBy("orderTime", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first() // Get the most recent document
                    val orderData = document.data
//This maps all the names from firebase
                    val name = orderData["restaurantName"] as? String ?: "No name"

                    binding.restaurantNameTextView.text = name


                    val addressPoint = orderData["addressPoint"] as? GeoPoint
                    Log.i("OrdersScreen", "Address point: $addressPoint")

                    val address = orderData["address"] as? String ?: "No address"
                    val specialInstructions = orderData["specialInstructions"] as? String ?: "No special instructions"
                    val orderTime = orderData["orderTime"] as? String ?: "No time available"
                    val qu = orderData["quantity"] as? String ?: "No time available"
                    Log.i("OrdersScreen", "Order: $qu")

                    // Assuming 'items' is a List of Maps
                    val itemsList = orderData["items"] as? List<Map<String, Any>> ?: emptyList()
                    val foodItemsWithQuantities = itemsList.mapNotNull { itemMap ->
                        val foodItemMap = itemMap["foodItem"] as? Map<String, Any>
                        val quantity = itemMap["quantity"] as? Long ?: 0L
                        foodItemMap?.let {
                            val name = it["name"] as? String ?: "Unknown"
                            val price = it["price"] as? Double ?: 0.0
                            Log.i("OrdersScreen", "Food item: $name, $price, $quantity")
                            SelectedFoodItem(FoodItem(name, price), quantity.toInt())
                        }
                    }



                    binding.trackOrderButton.setOnClickListener {

                        findNavController().navigate(R.id.action_ordersScreen_to_mapFragment)
                    }

                    // Use this list to display your items
                    displayOrder(foodItemsWithQuantities, address, specialInstructions, orderTime)
                } else {
                    Log.d("OrdersScreen", "No orders found")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("OrdersScreen", "Error getting documents: ", exception)
            }
    }

    private fun displayOrder(foodItemsWithQuantities: List<SelectedFoodItem>, address: String, specialInstructions: String, orderTime: String) {
        binding.foodItemsContainer.removeAllViews()

        // Display address and special instructions
        binding.addressTextView.text = address
        binding.specialInstructionsTextView.text = specialInstructions
        binding.orderTimeTextView.text = orderTime

        // Then display each food item with quantity and price
        foodItemsWithQuantities.forEach { selectedFoodItem ->
            val orderItemView = layoutInflater.inflate(R.layout.order_item_view, binding.foodItemsContainer, false)
            orderItemView.findViewById<TextView>(R.id.foodItemName).text = selectedFoodItem.foodItem.name
            orderItemView.findViewById<TextView>(R.id.orderItemPrice).text = getString(R.string.food_item_price, selectedFoodItem.foodItem.price)
            orderItemView.findViewById<TextView>(R.id.orderItemQuantity).text = selectedFoodItem.quantity.toString()

            binding.foodItemsContainer.addView(orderItemView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
