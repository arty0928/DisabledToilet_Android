package com.example.disabledtoilet_android.ToiletPlus

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.databinding.ActivityInputPlusToiletInfoBinding
import com.example.disabledtoilet_android.databinding.ActivityPlusToiletBinding

class ToiletPlusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlusToiletBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlusToiletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val checkBtn = binding.plusToiletCheckButton

        checkBtn.setOnClickListener {
            val intent = Intent(this, InputPlusToiletInputPageActivity::class.java)  // Activity 클래스를 사용해야 함
            startActivity(intent)
        }

        val backButton = binding.backButton
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}