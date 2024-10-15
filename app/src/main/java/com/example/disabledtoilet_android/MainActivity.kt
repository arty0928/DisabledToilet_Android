package com.example.disabledtoilet_android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.disabledtoilet_android.Near.NearActivity
import com.example.disabledtoilet_android.ToiletPlus.ToiletPlusActivity
import com.example.disabledtoilet_android.ToiletSearch.ToiletFilterSearchActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_main)

        // 내 주변 버튼
        val nearButton: LinearLayout = findViewById(R.id.near_button)
        nearButton.setOnClickListener {
            val intent = Intent(this, NearActivity::class.java)
            startActivity(intent)
        }

        // 장소 검색 버튼
        val searchButton: LinearLayout = findViewById(R.id.search_button)
        searchButton.setOnClickListener {
            val intent = Intent(this, ToiletFilterSearchActivity::class.java)
            startActivity(intent)
        }

        // 화장실 등록 버튼
        val plusToiletButton: LinearLayout = findViewById(R.id.toiletplus_button)
        plusToiletButton.setOnClickListener {
            val intent = Intent(this, ToiletPlusActivity::class.java)
            startActivity(intent)
        }

        drawerLayout = findViewById(R.id.drawer_layout_login)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        //NavigationView 너비를 화면의 80%로 설정
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val layoutParams = navigationView.layoutParams
        layoutParams.width =  (screenWidth * 0.35).toInt()
        navigationView.layoutParams = layoutParams


        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.my_toilet_button -> {
                    Toast.makeText(this, "mypage 아이템", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
            drawerLayout.closeDrawers()
            true
        }

        val menuIcon = findViewById<ImageView>(R.id.menu_icon_login)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        //로그아웃
        val headerView : View = navigationView.getHeaderView(0)
        val navLogoutButton : LinearLayout = headerView.findViewById(R.id.login_nav_logout_button)
        navLogoutButton.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        firebaseAuth.signOut()
        Toast.makeText(this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
        updateUI(null)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            val intent = Intent(this, NonloginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
