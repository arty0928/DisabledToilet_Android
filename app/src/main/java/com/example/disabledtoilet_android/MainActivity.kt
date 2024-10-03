package com.example.disabledtoilet_android

import android.annotation.SuppressLint
import com.kakao.sdk.common.util.Utility
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.disabledtoilet_android.Near.NearActivity
import com.kakao.sdk.common.KakaoSdk

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    private lateinit var drawerLayout: DrawerLayout


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoSdk.init(this, "ce27585c8cc7c468ac7c46901d87199d")
        setContentView(R.layout.activity_main)

        //내 주변
        val nearButton : LinearLayout = findViewById(R.id.near_button)

        nearButton.setOnClickListener{
            val intent = Intent(this, NearActivity::class.java)
            startActivity(intent)
        }

        //장소 검색
        val searchButton : LinearLayout = findViewById(R.id.search_button)

        searchButton.setOnClickListener{
            val intent = Intent(this, NearActivity::class.java)
            startActivity(intent)
        }

//        //화장실 등록
//        val plusToiletButton : LinearLayout = findViewById(R.id.plustoilet_button)
//
//        plusToiletButton.setOnClickListener {
//            val intent = Intent(this, LoginActivity::class.java )
//        }

        //메뉴 바
//        drawerLayout = findViewById(R.id.drawer_layout)
//        val menuButton : AppCompatImageView = findViewById(R.id.menu_button)
//
//        menuButton.setOnClickListener {
//            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                drawerLayout.closeDrawer(GravityCompat.START)
//            } else {
//                drawerLayout.openDrawer(GravityCompat.START)
//            }
//        }

        Log.d("KeyHash", "${Utility.getKeyHash(this)}")
    }
}