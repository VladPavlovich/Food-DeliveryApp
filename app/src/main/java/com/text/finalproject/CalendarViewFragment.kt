package com.text.finalproject

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.text.finalproject.databinding.FragmentCalendarViewBinding

class CalendarViewFragment : Fragment() {

    private var _binding: FragmentCalendarViewBinding? = null
    private val binding get() = _binding!!
    private val orderAmountsByDate = mutableMapOf<String, Double>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalendarViewBinding.inflate(inflater, container, false)

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = formatDate(year, month, dayOfMonth)
            Log.i("CalendarViewFragment", "Selected date: $date")
            updateAmountForDate(date)
        }

        loadOrderData()

        return binding.root
    }
//database retrieval
    private fun loadOrderData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("CheckoutScreen", "User is not logged in.")
            return
        }


//database retrieval
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).collection("orders")            .orderBy("orderTime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val orderData = document.data
                    val orderTime = orderData["orderTime"] as? String
                    val orderDate = orderTime?.substringBefore(" ") // Extracts only the date part
                    Log.i("CalendarViewFragment", "Order date: $orderDate")
                    val itemsList = orderData["items"] as? List<Map<String, Any>> ?: continue
                    val totalAmount = calculateTotalAmount(itemsList)
                    orderDate?.let { updateTimeToAmountMap(it, totalAmount) }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("CalendarViewFragment", "Error getting documents: ", exception)
            }
    }

    /*
    * Updates the orderAmountsByDate map with the given date and amount.
    * If the date already exists in the map, the amount is added to the existing amount.
    * param date: The date to update the map with
    * param amount: The amount to add to the map
     */
    private fun updateTimeToAmountMap(orderTime: String, amount: Double) {
        val date = orderTime.split(" ")[0] // Assuming orderTime is in 'yyyy-MM-dd HH:mm:ss' format
        orderAmountsByDate[date] = orderAmountsByDate.getOrDefault(date, 0.0) + amount
    }
/*
* Calculates the total amount for the given list of items.
* param itemsList: The list of items to calculate the total amount for
* return: The total amount for the given list of items
 */
    private fun calculateTotalAmount(itemsList: List<Map<String, Any>>): Double {
        return itemsList.sumOf { itemMap ->
            val quantity = itemMap["quantity"] as? Long ?: 0L
            Log.i("A", "Quantity: $quantity")
            val foodItemMap = itemMap["foodItem"] as? Map<String, Any>
            val price = foodItemMap?.get("price") as? Double ?: 0.0
            val orderTime = itemMap["orderTime"] as? String
            Log.i("B", "Price: $price")
            Log.i("C", "Order time: $orderTime")
            price * quantity
        }
    }

/*
* Updates the total amount text view with the total amount for the given date.
* param date: The date to get the total amount for
* return: The total amount for the given date
 */
    private fun updateAmountForDate(date: String) {
        val totalAmount = orderAmountsByDate[date] ?: 0.0
        Log.i("CalendarViewFragment", "Total amount for $date: $totalAmount")
        binding.totalAmountTextView.text = "Total Amount: $${"%.2f".format(totalAmount)}"
    }

    /*
* Formats the given year, month, and dayOfMonth into a string of the format 'yyyy-MM-dd'.
* param year: The year to format
* param month: The month to format
* param dayOfMonth: The dayOfMonth to format
* return: The formatted date string
     */

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        // CalendarView months are zero-based
        val formattedMonth = (month + 1).toString().padStart(2, '0')
        val formattedDay = dayOfMonth.toString().padStart(2, '0')
        return "$year-$formattedMonth-$formattedDay"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}