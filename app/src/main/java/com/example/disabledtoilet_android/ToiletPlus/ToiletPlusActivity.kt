package com.example.disabledtoilet_android.ToiletPlus

import android.location.Location
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.databinding.ActivityToiletPlusBinding
import com.google.android.gms.location.LocationListener

class ToiletPlusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityToiletPlusBinding
    private lateinit var lastLocation: Location


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityToiletPlusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                lastLocation = location
            }
        }

    }
}