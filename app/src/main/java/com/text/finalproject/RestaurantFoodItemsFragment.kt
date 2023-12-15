package com.text.finalproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.text.finalproject.databinding.FragmentRestaurantFoodItemsBinding
import kotlin.math.log

class RestaurantFoodItemsFragment : Fragment() {

    private var _binding: FragmentRestaurantFoodItemsBinding? = null
    private val binding get() = _binding!!

    // This list will hold the actual food items and their quantities
    private var foodItemsWithQuantities: MutableList<Pair<FoodItem, Int>> = mutableListOf()
    private var restaurantName: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantFoodItemsBinding.inflate(inflater, container, false)

        // Populate the view with actual food items
        foodItemsWithQuantities.forEach { (foodItem, quantity) ->
            addFoodItemView(foodItem, quantity)
        }

        binding.checkoutButton.setOnClickListener {
            navigateToCheckoutScreen()
        }

        return binding.root
    }

    // Method to set actual food items (call this method with actual food items before onCreateView)
    fun setFoodItems(items: List<Pair<FoodItem, Int>>) {
        foodItemsWithQuantities.clear()
        foodItemsWithQuantities.addAll(items)
    }

    fun setRestaurantName(name: String) {
        restaurantName = name
        Log.i("RestA", "Restaurant name: $restaurantName")
    }


    private fun navigateToCheckoutScreen() {
        val selectedFoodItems = foodItemsWithQuantities.map { SelectedFoodItem(it.first, it.second) }
        val foodOrder = FoodOrder(selectedFoodItems)

        Log.i("RestaurantFoodItemsFragment", "Navigating to CheckoutScreen with order: $foodOrder")
        Log.i("RestB", "Navigating to CheckoutScreen with order: $restaurantName")

        // Create the action with the provided restaurant name
        val action = RestaurantScreenDirections.actionRestaurantScreenToCheckoutScreen(foodOrder)

        findNavController().navigate(action)
    }

/*
addFoodItemView() is a method that takes a FoodItem and a quantity and adds a view to the
param foodItem: FoodItem
param quantity: Int
adds a view to the foodItemsContainer
 */
    private fun addFoodItemView(foodItem: FoodItem, quantity: Int) {
        val foodItemLayout = layoutInflater.inflate(R.layout.food_item_view, binding.foodItemsContainer, false)
        val foodNameTextView = foodItemLayout.findViewById<TextView>(R.id.foodItemName)
        val foodPriceTextView = foodItemLayout.findViewById<TextView>(R.id.foodItemPrice)
        val foodQuantityTextView = foodItemLayout.findViewById<TextView>(R.id.foodItemQuantity)
        val addButton = foodItemLayout.findViewById<Button>(R.id.addButton)
        val subtractButton = foodItemLayout.findViewById<Button>(R.id.subtractButton)

        foodNameTextView.text = foodItem.name
        foodPriceTextView.text = getString(R.string.food_item_price, foodItem.price)
        foodQuantityTextView.text = quantity.toString()

        addButton.setOnClickListener {
            val newQuantity = foodQuantityTextView.text.toString().toInt() + 1
            foodQuantityTextView.text = newQuantity.toString()
            updateFoodItemQuantity(foodItem.name, newQuantity)
        }

        subtractButton.setOnClickListener {
            val currentQuantity = foodQuantityTextView.text.toString().toInt()
            if (currentQuantity > 1) {
                val newQuantity = currentQuantity - 1
                foodQuantityTextView.text = newQuantity.toString()
                updateFoodItemQuantity(foodItem.name, newQuantity)
            }
        }

        binding.foodItemsContainer.addView(foodItemLayout)
    }
//updates the quantity of each food item
    private fun updateFoodItemQuantity(foodItemName: String, newQuantity: Int) {
        val index = foodItemsWithQuantities.indexOfFirst { it.first.name == foodItemName }
        if (index != -1) {
            val foodItem = foodItemsWithQuantities[index].first
            foodItemsWithQuantities[index] = Pair(foodItem, newQuantity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
