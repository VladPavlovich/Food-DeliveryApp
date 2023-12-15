package com.text.finalproject

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.text.finalproject.databinding.FragmentCheckoutScreenBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*


class CheckoutScreen : Fragment() {

    private var _binding: FragmentCheckoutScreenBinding? = null
    private val binding get() = _binding!!

    private var foodOrder: FoodOrder? = null
    val viewModel : SharedViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheckoutScreenBinding.inflate(inflater, container, false)

        // Retrieve the FoodOrder passed to this fragment
        foodOrder = arguments?.getParcelable("foodOrder")
        Log.i("CheckoutScreen", "Food order: $foodOrder")

        // Dynamically add views for each SelectedFoodItem in the FoodOrder
        foodOrder?.itemsWithQuantities?.forEach { selectedFoodItem ->
            addFoodItemToCheckout(selectedFoodItem)
        }

        // Setup Place Order Button
        binding.placeOrderButton.setOnClickListener { placeOrder() }

        binding.modifyOrderButton.setOnClickListener {
            findNavController().navigate(R.id.action_checkoutScreen_to_restaurantScreen)
        }

        return binding.root
    }

//    data class Order(
//        val items: List<SelectedFoodItem>,
//        val address: String,
//        val specialInstructions: String,
//        val orderTime: String,
//        val deliveryTime: Double
//    )


    private fun addFoodItemToCheckout(selectedFoodItem: SelectedFoodItem) {
        val foodItemView = layoutInflater.inflate(R.layout.food_item_checkout_view, binding.foodItemsContainer, false)

        val foodName = foodItemView.findViewById<TextView>(R.id.foodItemName)
        val foodPrice = foodItemView.findViewById<TextView>(R.id.foodItemPrice)
        val quantityTextView = foodItemView.findViewById<TextView>(R.id.foodItemQuantity)
        val addButton = foodItemView.findViewById<Button>(R.id.addButton)
        val subtractButton = foodItemView.findViewById<Button>(R.id.subtractButton)

        foodName.text = selectedFoodItem.foodItem.name
        foodPrice.text = getString(R.string.food_item_price, selectedFoodItem.foodItem.price)
        quantityTextView.text = selectedFoodItem.quantity.toString()
//quantity
        addButton.setOnClickListener {
            val newQuantity = quantityTextView.text.toString().toInt() + 1
            quantityTextView.text = newQuantity.toString()
            updateFoodItemQuantity(selectedFoodItem.foodItem.name, newQuantity)
        }

        subtractButton.setOnClickListener {
            val currentQuantity = quantityTextView.text.toString().toInt()
            if (currentQuantity > 1) {
                val newQuantity = currentQuantity - 1
                quantityTextView.text = newQuantity.toString()
                updateFoodItemQuantity(selectedFoodItem.foodItem.name, newQuantity)
            }
        }

        binding.foodItemsContainer.addView(foodItemView)
    }
/*
* Updates the quantity of the food item with the given name.
* param foodItemName: The name of the food item to update
* param newQuantity: The new quantity of the food item
 */
    private fun updateFoodItemQuantity(foodItemName: String, newQuantity: Int) {
        foodOrder?.itemsWithQuantities?.find { it.foodItem.name == foodItemName }?.let {
            it.quantity = newQuantity
        }
    }
/*
* Gets the GeoPoint for the given address string.
* param addressString: The address string to get the GeoPoint for
* return: The GeoPoint for the given address string
 */
    private fun getAddressGeoPoint(addressString: String): GeoPoint? {
        val geocoder = context?.let { Geocoder(it) }
        try {
            val addressList = geocoder?.getFromLocationName(addressString, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList[0]
                return GeoPoint(address.latitude, address.longitude)
            }
        } catch (e: IOException) {
            Log.e("GeocoderError", "Error getting location: $e")
        }
        return null
    }


    fun distance(geoPoint1: GeoPoint,geoPoint2: GeoPoint): Double{

        val R = 6371.0 // Radius of the Earth in kilometers

        val lat1 = Math.toRadians(geoPoint1.latitude)
        val lon1 = Math.toRadians(geoPoint1.longitude)
        val lat2 = Math.toRadians(geoPoint2.latitude)
        val lon2 = Math.toRadians(geoPoint2.longitude)

        Log.i("CheckoutScreen", "lat1: $lat1")
        Log.i("CheckoutScreen", "lon1: $lon1")
        Log.i("CheckoutScreen", "lat2: $lat2")
        Log.i("CheckoutScreen", "lon2: $lon2")


        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * asin(sqrt(a))

        return R

    }


//calculates the delivery time in milliseconds for the notificatins
    private fun calculateDeliveryTimeInMillis(deliveryTime: Double): Long {
        // Convert deliveryTime from minutes to milliseconds
        return System.currentTimeMillis() + (deliveryTime * 60000).toLong()
    }
//starts the delivery service
//    private fun startDeliveryService(deliveryTimeInMillis: Long) {
//        val intent = Intent(context, DeliveryService::class.java)
//        intent.putExtra("deliveryTimeInMillis", deliveryTimeInMillis)
//        context?.startService(intent)
//    }



//places the order
    private fun placeOrder() {
        val address = binding.deliveryAddress.text.toString()
        val specialInstructions = binding.specialInstructions.text.toString()

        val currentTime = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = formatter.format(currentTime)
        val name = viewModel.restaurantName
        val location = viewModel.restaurantLocation
        val userAdress = getAddressGeoPoint(address)
        val distance = distance(location!!,userAdress!!)

        // formula: (Distance / 100) * (Random number between 5 and 100)
        viewModel.addressLocation(userAdress)

        val deliveryTime = (distance/100) * (5 + Random().nextInt(96))
        Log.i("CheckoutScreen", "Delivery time: $deliveryTime")
        Log.i("CheckoutScreen", "Distance: $distance")
        viewModel.estimatedTime(deliveryTime)

    val deliveryTimeInMillis = System.currentTimeMillis() + (deliveryTime * 60000).toLong()

    // Start NotificationService with the delivery time
    val intent = Intent(context, NotificationService::class.java)
    intent.putExtra("deliveryTimeInMillis", deliveryTimeInMillis)
    context?.startService(intent)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("CheckoutScreen", "User is not logged in.")
            return
        }


        val order = Order(
            items = foodOrder?.itemsWithQuantities.orEmpty(),
            address = address,
            specialInstructions = specialInstructions,
            orderTime = formattedDate,
            deliveryTime = deliveryTime,
            restaurantName = name!!,
            restaurantLocation = location!!,
            adressPoint = userAdress!!
        )

        // Save order to Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).collection("orders")            .add(order)
            .addOnSuccessListener { documentReference ->
                Log.d("CheckoutScreen", "Order placed with ID: ${documentReference.id}")
                // Navigate to Orders screen
                findNavController().navigate(R.id.action_checkoutScreen_to_ordersScreen)
            }
            .addOnFailureListener { e ->
                Log.w("CheckoutScreen", "Error adding order", e)
                // Handle the error
            }

        Log.i("CheckoutScreen", "Order placed at $formattedDate with delivery time: $deliveryTime minutes")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
