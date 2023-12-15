package com.text.finalproject

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {

    private val viewModel: ViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onStart() {
        super.onStart()
        checkIfUserIsLoggedIn()
    }

    private fun checkIfUserIsLoggedIn() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        Handler(Looper.getMainLooper()).postDelayed({
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.splashFragment, true) // This clears the back stack
                .build()

            if (currentUser != null) {
                findNavController().navigate(R.id.action_splashFragment_to_homeScreen, null, navOptions)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_logInScreen, null, navOptions)
            }
        }, 0) // Adjust delay time if needed
    }

}