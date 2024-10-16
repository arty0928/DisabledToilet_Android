package com.example.disabledtoilet_android.ToiletPlus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.disabledtoilet_android.databinding.ActivityInputPlusToiletInfoBinding

class InputPlusToiletInputPageActivity : ComponentActivity() {

    private lateinit var binding: ActivityInputPlusToiletInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityInputPlusToiletInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val backButton = binding.backButton
        backButton.setOnClickListener {
            onBackPressed()
        }


    }
}

