package com.example.disabledtoilet_android

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.disabledtoilet_android.Near.NearActivity
import com.example.disabledtoilet_android.ToiletPlus.ToiletPlusActivity
import com.example.disabledtoilet_android.ToiletSearch.ToiletFilterSearchActivity
import com.google.android.material.navigation.NavigationView
import com.kakao.sdk.common.KakaoSdk

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoSdk.init(this, "ce27585c8cc7c468ac7c46901d87199d")
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        // 내 주변
        val nearButton: LinearLayout = findViewById(R.id.near_button)
        nearButton.setOnClickListener {
            val intent = Intent(this, NearActivity::class.java)
            startActivity(intent)
        }

        // 장소 검색
        val searchButton: LinearLayout = findViewById(R.id.search_button)
        searchButton.setOnClickListener {
            val intent = Intent(this, ToiletFilterSearchActivity::class.java)
            startActivity(intent)
        }

        // 화장실 등록
        val plusToiletButton: LinearLayout = findViewById(R.id.toiletplus_button)
        plusToiletButton.setOnClickListener {
            val intent = Intent(this, ToiletPlusActivity::class.java)
            startActivity(intent)
        }

        // DrawerLayout과 NavigationView 초기화
        drawerLayout = findViewById(R.id.drawer_layout_login) // DrawerLayout 초기화
        navigationView = findViewById(R.id.nav_view) // NavigationView 초기화

        //NavigationView 너비를 화면의 80%로 설정
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val layoutParams = navigationView.layoutParams
        layoutParams.width =  (screenWidth * 0.35).toInt()
        navigationView.layoutParams = layoutParams

        //NavigationView 아이템 클릭 리스너 설정
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val handled = when (menuItem.itemId) {
                R.id.nav_plusItem_icon -> {
                    // 해당 아이템이 선택되었을 때 수행할 작업
                    Toast.makeText(this, "Plus Item 클릭됨", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }

            // 메뉴 아이템이 선택된 후, 드로어를 닫음
            drawerLayout.closeDrawers()

            // handled 값 반환
            handled
        }

        // 메뉴 아이콘 클릭 시 Drawer 열기
        val menuIcon = findViewById<ImageView>(R.id.menu_icon_login)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        
    }
}
