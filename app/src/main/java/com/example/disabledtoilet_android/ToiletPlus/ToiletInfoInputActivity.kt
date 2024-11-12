package com.example.disabledtoilet_android.ToiletPlus

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.databinding.ActivityToiletInfoInputBinding
import com.kakao.vectormap.LatLng

class ToiletInfoInputActivity : AppCompatActivity() {
    lateinit var binding: ActivityToiletInfoInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToiletInfoInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인텐트에서 좌표값 추출
        getCoordinateFromIntent()
    }
    /**
     * intet에서 좌표값 갖고 오기
     */
    private fun getCoordinateFromIntent(): LatLng{
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        return LatLng.from(latitude, longitude)
    }
}