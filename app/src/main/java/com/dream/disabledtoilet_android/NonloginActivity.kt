package com.dream.disabledtoilet_android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.dream.disabledtoilet_android.Near.NearActivity
import com.dream.disabledtoilet_android.ToiletSearch.ToiletFilterSearchActivity
import com.dream.disabledtoilet_android.Utility.Dialog.utils.GoogleHelper
import com.dream.disabledtoilet_android.databinding.ActivityNonloginBinding
import com.google.android.material.navigation.NavigationView

class NonloginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNonloginBinding
    lateinit var googleSignInHelper: GoogleHelper
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNonloginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        googleSignInHelper = GoogleHelper.getInstance(this) // Singleton 인스턴스 가져오기
        initializeUI() // UI 초기화
    }

    private fun initializeUI() {
        binding.nearButton.setOnClickListener {
            startActivity(Intent(this, NearActivity::class.java))
        }

        binding.searchButton.setOnClickListener {
            startActivity(Intent(this, ToiletFilterSearchActivity::class.java))
        }

        //TODO: 출시 전 로그인 막기
//        setupNavigationDrawer() // 내비게이션 드로어 설정
    }

    private fun setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout_nonlogin)
        val navigationView: NavigationView = findViewById(R.id.nav_view_after_login)

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val layoutParams = navigationView.layoutParams
        layoutParams.width = (screenWidth * 0.3).toInt()
        navigationView.layoutParams = layoutParams

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_icon1 -> {
                    googleSignInHelper.startLoginGoogle(googleLoginResult) // 구글 로그인 시작
                    true
                }
                else -> false
            }
        }

        val headerView: View = navigationView.getHeaderView(0)
        val navLogInButton: LinearLayout = headerView.findViewById(R.id.loginbtn)
        navLogInButton.setOnClickListener {
            googleSignInHelper.startLoginGoogle(googleLoginResult) // 로그인 처리
        }

        findViewById<ImageView>(R.id.menu_icon).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    val googleLoginResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        googleSignInHelper.handleSignInResult(data) { account ->
            googleSignInHelper.checkUserInFirestore(account)
        }
    }
}
