package com.example.disabledtoilet_android

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class NearActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near)

        // Find the ImageButton by its ID
        val backButton: ImageButton = findViewById(R.id.back_button)

        // Set an OnClickListener to call onBackPressed() when the button is clicked
        backButton.setOnClickListener {
            onBackPressed()
        }

        val mapButton: ImageButton = findViewById(R.id.map_icon)

        // Set an OnClickListener to call onBackPressed() when the button is clicked
        backButton.setOnClickListener {
            val intent = Intent(this, NearActivity::class.java)
            startActivity(intent)
        }
    }
}
