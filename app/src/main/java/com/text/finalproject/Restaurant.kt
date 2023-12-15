package com.text.finalproject
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.tasks.await
import java.util.*

data class Restaurant(
    val name: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val foodItems: List<FoodItem> = emptyList(),
    val images: List<String> = emptyList()  // Assuming you have images
)

@Parcelize
data class FoodItem(
    val name: String = "",
    val price: Double = 0.0
): Parcelable


@Parcelize
data class SelectedFoodItem(
    val foodItem: FoodItem,
    var quantity: Int
) : Parcelable


@Parcelize
data class FoodOrder(
    val itemsWithQuantities: List<SelectedFoodItem>
) : Parcelable


data class Order(
    val items: List<SelectedFoodItem> = emptyList(),
    val address: String = "",
    val specialInstructions: String = "",
    val orderTime: String = "",
    val deliveryTime: Double = 0.0,
    val restaurantName: String = "",
    val restaurantLocation: GeoPoint = GeoPoint(0.0, 0.0),
    val adressPoint: GeoPoint = GeoPoint(0.0, 0.0),
)


class FirebaseDataUploader {

    private val db = FirebaseFirestore.getInstance()

    suspend fun uploadData() {
        val restaurants = getPredefinedRestaurants()

        restaurants.forEach { restaurant ->
            addRestaurantToFirestore(restaurant)
        }
    }

    private fun getPredefinedRestaurants(): List<Restaurant> {
        return listOf(
            Restaurant(
                name = "Taco Bell",
                location = GeoPoint(39.168941, -86.533958),
                foodItems = listOf(
                    FoodItem(name = "Taco", price = 2.99),
                    FoodItem(name = "Burrito", price = 3.99),
                    FoodItem(name = "Nachos", price = 1.99),
                    FoodItem(name = "Quesadilla", price = 4.99),
                    FoodItem(name = "Chalupa", price = 2.99),

                )
            ),
            Restaurant(
                name = "McDonald's",
                location = GeoPoint(39.187149, -86.533699),
                foodItems = listOf(
                    FoodItem(name = "Big Mac", price = 5.99),
                    FoodItem(name = "Fries", price = 1.99),
                    FoodItem(name = "McFlurry", price = 2.99),
                    FoodItem(name = "McNuggets", price = 4.99),
                    FoodItem(name = "Quarter Pounder", price = 5.99),

                )
            ),
            Restaurant(
                name = "Burger King",
                location = GeoPoint(39.164130, -86.498060),
                foodItems = listOf(
                    FoodItem(name = "Whopper", price = 5.99),
                    FoodItem(name = "Fries", price = 1.99),
                    FoodItem(name = "Onion Rings", price = 2.99),
                    FoodItem(name = "Chicken Sandwich", price = 4.99),
                    FoodItem(name = "Chicken Fries", price = 3.99),
                )
            ),
            Restaurant(
                name = "Wendy's",
                location = GeoPoint(39.1611049, -86.535754),
                foodItems = listOf(
                    FoodItem(name = "Baconator", price = 5.99),
                    FoodItem(name = "Fries", price = 1.99),
                    FoodItem(name = "Frosty", price = 2.99),
                    FoodItem(name = "Chicken Sandwich", price = 4.99),
                    FoodItem(name = "Chicken Nuggets", price = 3.99),
                )
            ),
Restaurant(
                name = "Panda Express",
                location = GeoPoint(39.665288, -86.0836608),
                foodItems = listOf(
                    FoodItem(name = "Orange Chicken", price = 5.99),
                    FoodItem(name = "Fried Rice", price = 1.99),
                    FoodItem(name = "Chow Mein", price = 2.99),
                    FoodItem(name = "Egg Rolls", price = 4.99),
                    FoodItem(name = "Beijing Beef", price = 3.99),
                )
            ),
Restaurant(
                name = "Chick-fil-A",
                location = GeoPoint(39.15047, -86.498244),
                foodItems = listOf(
                    FoodItem(name = "Chicken Sandwich", price = 5.99),
                    FoodItem(name = "Waffle Fries", price = 1.99),
                    FoodItem(name = "Nuggets", price = 2.99),
                    FoodItem(name = "Milkshake", price = 4.99),
                    FoodItem(name = "Spicy Chicken Sandwich", price = 3.99),
                )
            ),
Restaurant(
                name = "Chipotle",
                location = GeoPoint(39.166300, -86.529030),
                foodItems = listOf(
                    FoodItem(name = "Burrito", price = 5.99),
                    FoodItem(name = "Bowl", price = 1.99),
                    FoodItem(name = "Tacos", price = 2.99),
                    FoodItem(name = "Chips", price = 4.99),
                    FoodItem(name = "Quesadilla", price = 3.99),
                )
            ),
Restaurant(
                name = "Starbucks",
                location = GeoPoint(39.1453563, -86.5321055),
                foodItems = listOf(
                    FoodItem(name = "Coffee", price = 5.99),
                    FoodItem(name = "Latte", price = 1.99),
                    FoodItem(name = "Frappuccino", price = 2.99),
                    FoodItem(name = "Tea", price = 4.99),
                    FoodItem(name = "Cake Pop", price = 3.99),
                )
            ),

        )
    }



    private fun addRestaurantToFirestore(restaurant: Restaurant) {
        db.collection("restaurants").add(restaurant)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }
}
