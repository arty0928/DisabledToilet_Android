package com.example.disabledtoilet_android.ToiletPlus

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.databinding.ActivityToiletInfoInputBinding

class ToiletInfoInputActivity : AppCompatActivity() {
    lateinit var binding: ActivityToiletInfoInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToiletInfoInputBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}