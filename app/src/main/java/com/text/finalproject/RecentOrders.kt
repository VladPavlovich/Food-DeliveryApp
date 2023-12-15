package com.text.finalproject

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query


class RecentOrders : Fragment() {
    private lateinit var ordersAdapter: OrdersAdapter
    private lateinit var ordersRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        loadOrders()
    }

    private fun setupRecyclerView(view: View) {
        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(context)
        ordersAdapter = OrdersAdapter(emptyList())
        ordersRecyclerView.adapter = ordersAdapter
    }

    private fun updateRecyclerView(orders: List<Order>) {
        ordersAdapter = OrdersAdapter(orders)
        ordersRecyclerView.adapter = ordersAdapter
    }
    private fun loadOrders() {

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("CheckoutScreen", "User is not logged in.")
            return
        }

//maps all the orders from firebase
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).collection("orders")
            .orderBy("orderTime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val orders = documents.mapNotNull { document ->
                        val orderData = document.data
                        val restaurantName = orderData["restaurantName"] as? String ?: "No name"
                        val address = orderData["address"] as? String ?: "No address"
                        val specialInstructions = orderData["specialInstructions"] as? String ?: "No special instructions"
                        val orderTime = orderData["orderTime"] as? String ?: "No time available"
                        val deliveryTime = orderData["deliveryTime"] as? Double ?: 0.0
                        val restaurantLocation = orderData["restaurantLocation"] as? GeoPoint ?: GeoPoint(0.0, 0.0)
                        val addressPoint = orderData["addressPoint"] as? GeoPoint ?: GeoPoint(0.0, 0.0)

                        // Parse 'items' list
                        val itemsList = orderData["items"] as? List<Map<String, Any>> ?: emptyList()
                        val selectedItems = itemsList.mapNotNull { itemMap ->
                            val foodItemMap = itemMap["foodItem"] as? Map<String, Any>
                            val quantity = itemMap["quantity"] as? Long ?: 0L
                            foodItemMap?.let {
                                val name = it["name"] as? String ?: "Unknown"
                                val price = it["price"] as? Double ?: 0.0
                                SelectedFoodItem(FoodItem(name, price), quantity.toInt())
                            }
                        }

                        Order(
                            items = selectedItems,
                            address = address,
                            specialInstructions = specialInstructions,
                            orderTime = orderTime,
                            deliveryTime = deliveryTime,
                            restaurantName = restaurantName,
                            restaurantLocation = restaurantLocation,
                           // addressPoint = addressPoint
                        )
                    }
                    // Update RecyclerView with this list
                    updateRecyclerView(orders)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("RecentOrdersFragment", "Error getting documents: ", exception)
            }
    }

}