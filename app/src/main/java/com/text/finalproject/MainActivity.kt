package com.text.finalproject

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

  private val viewModel: SharedViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        // top-level destinations for the navigation drawer to work
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeScreen,R.id.recentOrders,R.id.calendarViewFragment,R.id.mapFragment,R.id.ordersScreen,R.id.checkoutScreen,R.id.restaurantScreen), // Add more destinations as needed
            drawer
        )


        //set up Toolbar with NavController and AppBarConfiguration
        toolbar.setupWithNavController(navController, appBarConfiguration)

        // Link the drawer to the navigation controller
        val navView = findViewById<NavigationView>(R.id.nav_view)
        NavigationUI.setupWithNavController(navView, navController)

        // sets up the navigation drawer
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Home -> {
                    navController.navigate(R.id.homeScreen)
                }

                R.id.recentOrders -> {
                    navController.navigate(R.id.recentOrders)
                }

                R.id.calender -> {
                    navController.navigate(R.id.calendarViewFragment)
                }

                R.id.SignOut -> {
                    //auth.signOut()
                    viewModel.signOut()
                    navController.navigate(R.id.logInScreen)
                }



                // other cases
            }
            true
        }

        observeUserImage()
        observeEmail()
    }
    private fun observeUserImage() {
        viewModel.imageUri.observe(this, { uri ->
            uri?.let { updateUserImage(it) }
        })
    }
    private fun observeEmail() {
        viewModel.email.observe(this, { email ->
            updateEmailInHeader(email)
        })
    }
    // Update the email in the header
    private fun updateEmailInHeader(email: String?) {
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        val emailTextView: TextView = headerView.findViewById(R.id.user_name) // Replace with your TextView ID

        email?.let {
            emailTextView.text = it
        }
    }

// Update the image in the header
    private fun updateUserImage(uri: Uri) {
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        val userImageView: ImageView = headerView.findViewById(R.id.user_image)


        // Use Glide to load the image
        Glide.with(this)
            .load(uri)
            .into(userImageView)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}
