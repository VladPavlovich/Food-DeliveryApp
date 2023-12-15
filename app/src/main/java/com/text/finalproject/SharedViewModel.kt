package com.text.finalproject

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint


class SharedViewModel : ViewModel() {
    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }



    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val userLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    val errorLiveData: MutableLiveData<String> = MutableLiveData()

    //firebase register

    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userLiveData.postValue(auth.currentUser)
                } else {
                    errorLiveData.postValue(task.exception?.message)
                }
            }
    }

//firebase signout
    fun signOut(){
        auth.signOut()

    }

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email
//saves email
    fun saveEmail(email: String) {
        _email.value = email
        Log.i("SharedViewModel", "Email: $email")
    }



    var restaurantName: String? = null
    var restaurantLocation: GeoPoint? = null
    var userAddress: GeoPoint? = null
    var DeliveryTime: Double? = null
//saves restaurant details
    fun saveRestaurantDetails(name: String, location: GeoPoint) {
        restaurantName = name
        restaurantLocation = location
        Log.i("SharedViewModel", "Restaurant name: $restaurantName")
        Log.i("SharedViewModel", "Restaurant location: $restaurantLocation")
    }
    fun addressLocation(location: GeoPoint){
        userAddress = location
    }
//saves estimated time
    fun estimatedTime(deliveryTime: Double){
        DeliveryTime = deliveryTime
    }
//clears restaurant details when app is back to home screen
    fun clearRestaurantDetails() {
        restaurantName = null
        restaurantLocation = null
        userAddress = null
        DeliveryTime = null
Log.i("SharedViewModel", "Restaurant name: $restaurantName")
        Log.i("SharedViewModel", "Restaurant location: $restaurantLocation")
        Log.i("SharedViewModel", "Restaurant location: $userAddress")
        Log.i("SharedViewModel", "Restaurant location: $DeliveryTime")
    }






}