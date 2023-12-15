package com.text.finalproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.text.finalproject.databinding.FragmentMapBinding

class MapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentMapBinding.inflate(inflater, container, false)

        binding.btnNavigate.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_homeScreen)

        }

        binding.txtETA.text = viewModel.DeliveryTime.toString()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val userLocation = viewModel.userAddress?.let { LatLng(it.latitude, it.longitude) }
        val restaurantLocation = viewModel.restaurantLocation?.let { LatLng(it.latitude, it.longitude) }

        // Add markers
        userLocation?.let { googleMap.addMarker(MarkerOptions().position(it).title("User Location")) }
        restaurantLocation?.let { googleMap.addMarker(MarkerOptions().position(it).title("Restaurant Location")) }

        // Draw polyline between the two points
        if (userLocation != null && restaurantLocation != null) {
            val polylineOptions = com.google.android.gms.maps.model.PolylineOptions()
                .add(userLocation)
                .add(restaurantLocation)
                .color(android.graphics.Color.BLUE)
                .width(10f)

            googleMap.addPolyline(polylineOptions)

            // Zoom in on the map
            val boundsBuilder = com.google.android.gms.maps.model.LatLngBounds.Builder()
            boundsBuilder.include(userLocation)
            boundsBuilder.include(restaurantLocation)
            val bounds = boundsBuilder.build()

            val padding = 100 // offset from edges of the map in pixels
            val cameraUpdate = com.google.android.gms.maps.CameraUpdateFactory.newLatLngBounds(bounds, padding)
            googleMap.animateCamera(cameraUpdate)
        }
    }

}
