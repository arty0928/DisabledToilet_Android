package com.example.disabledtoilet_android

import com.kakao.sdk.common.util.Utility
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.disabledtoilet_android.Near.NearActivity
import com.example.disabledtoilet_android.ToiletPlus.ToiletPlusActivity
import com.example.disabledtoilet_android.ToiletSearch.ToiletFilterSearchActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.KakaoSdk

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoSdk.init(this, "ce27585c8cc7c468ac7c46901d87199d")
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        //내 주변
        val nearButton : LinearLayout = findViewById(R.id.near_button)

        nearButton.setOnClickListener{
            val intent = Intent(this, NearActivity::class.java)
            startActivity(intent)
        }

        //장소 검색
        val searchButton : LinearLayout = findViewById(R.id.search_button)

        searchButton.setOnClickListener{
            val intent = Intent(this, ToiletFilterSearchActivity::class.java)
            startActivity(intent)
        }

        //화장실 등록
        val plusToiletButton : LinearLayout = findViewById(R.id.toiletplus_button)

        plusToiletButton.setOnClickListener {
            val intent = Intent(this, ToiletPlusActivity::class.java )
            startActivity(intent)
        }

        Log.d("KeyHash", "${Utility.getKeyHash(this)}")

    }
}