package com.text.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RestaurantAdapter(private val onRestaurantClick: (Restaurant) -> Unit) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    private var restaurants = listOf<Restaurant>()

    fun submitList(newRestaurants: List<Restaurant>) {
        restaurants = newRestaurants
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(restaurants[position])
    }

    override fun getItemCount(): Int = restaurants.size

    inner class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(restaurant: Restaurant) {
            itemView.findViewById<TextView>(R.id.restaurantName).text = restaurant.name
            // Set other views in itemView
            itemView.setOnClickListener { onRestaurantClick(restaurant) }
        }
    }
}
