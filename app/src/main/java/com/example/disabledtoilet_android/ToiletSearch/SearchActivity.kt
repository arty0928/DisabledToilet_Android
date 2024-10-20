package com.example.disabledtoilet_android.ToiletSearch

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.disabledtoilet_android.R

class SearchActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Find the ImageButton by its ID
        val backButton: ImageButton = findViewById(R.id.back_button)

        // Set an OnClickListener to call onBackPressed() when the button is clicked
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}
