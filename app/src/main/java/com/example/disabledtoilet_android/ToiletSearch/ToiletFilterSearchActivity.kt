package com.example.disabledtoilet_android.ToiletSearch

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.databinding.ActivityToiletFilterSearchBinding

class ToiletFilterSearchActivity : AppCompatActivity() {
    lateinit var binding: ActivityToiletFilterSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToiletFilterSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}