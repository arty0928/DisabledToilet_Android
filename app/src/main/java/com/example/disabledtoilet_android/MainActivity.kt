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
import com.example.disabledtoilet_android.ToiletSearch.ToiletData
import com.example.disabledtoilet_android.ToiletSearch.ToiletFilterSearchActivity
import com.example.disabledtoilet_android.User.MyPageActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        initializeUI()

    }

    private fun initializeUI() {
        val nearButton: LinearLayout = findViewById(R.id.near_button)
        nearButton.setOnClickListener {
            startActivity(Intent(this, NearActivity::class.java))
        }

        val searchButton: LinearLayout = findViewById(R.id.search_button)
        searchButton.setOnClickListener {
            startActivity(Intent(this, ToiletFilterSearchActivity::class.java))
        }

        val plusToiletButton: LinearLayout = findViewById(R.id.toiletplus_button)
        plusToiletButton.setOnClickListener {
            startActivity(Intent(this, ToiletPlusActivity::class.java))
        }

        drawerLayout = findViewById(R.id.drawer_layout_login)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_mypage -> {
                    startActivity(Intent(this, MyPageActivity::class.java))
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }

        findViewById<ImageView>(R.id.menu_icon_login).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun signOut() {
        firebaseAuth.signOut()
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, NonloginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })
        finish()
    }
}