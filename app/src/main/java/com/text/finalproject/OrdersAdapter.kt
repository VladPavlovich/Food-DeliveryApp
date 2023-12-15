package com.text.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrdersAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantNameTextView: TextView = view.findViewById(R.id.restaurantNameTextView)
        val orderTimeTextView: TextView = view.findViewById(R.id.orderTimeTextView)
        val totalAmountTextView: TextView = view.findViewById(R.id.totalAmountTextView)
        val foodNameTextView: TextView = view.findViewById(R.id.foodName)
//        val foodPriceTextView: TextView = view.findViewById(R.id.foodPrice)
//        val foodQuantityTextView: TextView = view.findViewById(R.id.foodQuantity)
        val addressTextView: TextView = view.findViewById(R.id.addressTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.restaurantNameTextView.text = order.restaurantName
        holder.orderTimeTextView.text = order.orderTime
        holder.totalAmountTextView.text = "Total: ${calculateTotal(order)}"

        // Concatenate details of all food items
        val foodItemsDetails = buildString {
            order.items.forEach { item ->
                append("Name: ${item.foodItem.name}, Price: ${item.foodItem.price}, Quantity: ${item.quantity}\n")
            }
        }

        holder.foodNameTextView.text = foodItemsDetails
        holder.addressTextView.text = order.address
    }

    override fun getItemCount() = orders.size

    private fun calculateTotal(order: Order): Double {
        return order.items.sumOf { it.foodItem.price * it.quantity }
    }
}
