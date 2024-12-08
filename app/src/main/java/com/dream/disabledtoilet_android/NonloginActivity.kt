package com.dream.disabledtoilet_android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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

        if(getLocationPermission()){
            initializeUI()
        }
    }

    /**
     * 권한 받기 실행 함수
     */
    private fun getLocationPermission(): Boolean{
        var isGranted = false
        // 권한이 기존에 있는지 확인
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 기존에 없으면 받음
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
        } else {
            isGranted = true
        }
        return isGranted
    }
    /**
     * 위치 권한 받았을 때 콜백
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // 권한 승인
                    initializeUI()
                } else {
                    // 권한 미승인
                    onBackPressed()
                }
                return
            }
        }
    }

    private fun initializeUI() {
        binding.nearButton.setOnClickListener {
            startActivity(Intent(this, NearActivity::class.java))
        }

        binding.searchButton.setOnClickListener {
            startActivity(Intent(this, ToiletFilterSearchActivity::class.java))
        }

        setupNavigationDrawer() // 내비게이션 드로어 설정
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
            Log.d("nonlogin", "Menu item clicked: ${menuItem.itemId}")
            googleSignInHelper.startLoginGoogle(googleLoginResult) // 구글 로그인 시작
            true
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
