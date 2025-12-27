package com.mainstation.app.ui.screens.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mainstation.app.R

class LocationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_locations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            
            // Dummy Locations: Bantul, Sleman, Yogyakarta, Gunung Kidul
            val yogyakarta = LatLng(-7.7956, 110.3695)
            val bantul = LatLng(-7.8878, 110.3274)
            val sleman = LatLng(-7.7126, 110.3533)
            val gunungKidul = LatLng(-7.9620, 110.6062) // Wonosari area
            
            googleMap.addMarker(MarkerOptions().position(yogyakarta).title("MainSpace Offline - Yogyakarta"))
            googleMap.addMarker(MarkerOptions().position(bantul).title("MainSpace Offline - Bantul"))
            googleMap.addMarker(MarkerOptions().position(sleman).title("MainSpace Offline - Sleman"))
            googleMap.addMarker(MarkerOptions().position(gunungKidul).title("MainSpace Offline - Gunung Kidul"))
            
            // Move camera to Yogyakarta
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yogyakarta, 10f))
        }
    }
}
