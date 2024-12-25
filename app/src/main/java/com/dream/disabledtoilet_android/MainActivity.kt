package com.dream.disabledtoilet_android

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.util.Log
import android.widget.Toast
import com.dream.disabledtoilet_android.Near.UILayer.NearActivity
import com.dream.disabledtoilet_android.ToiletPlus.ToiletPlusActivity
import com.dream.disabledtoilet_android.ToiletSearch.ToiletFilterSearchActivity
import com.dream.disabledtoilet_android.User.MyPageActivity
import com.dream.disabledtoilet_android.Utility.Dialog.utils.GoogleHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var googleSignInHelper: GoogleHelper
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        googleSignInHelper = GoogleHelper.getInstance(this) // Singleton 인스턴스 가져오기
        initializeUI()
    }

    private fun initializeUI() {
        updateHeader() // 헤더 UI 업데이트

        setupNearButton() // '근처 화장실' 버튼 설정
        setupSearchButton() // '화장실 검색' 버튼 설정
        setupPlusToiletButton() // '화장실 추가' 버튼 설정
        setupMyPageButton() // '내 장소' 버튼 설정

        setupDrawerLayout() // DrawerLayout 및 NavigationView 설정
        setupMenuIcon() // 메뉴 아이콘 클릭 리스너 설정
    }

    private fun setupNearButton() {
        val nearButton: LinearLayout = findViewById(R.id.near_button)
        nearButton.setOnClickListener {
            startActivity(Intent(this, NearActivity::class.java))
        }
    }

    private fun setupSearchButton() {
        val searchButton: LinearLayout = findViewById(R.id.search_button)
        searchButton.setOnClickListener {
            startActivity(Intent(this, ToiletFilterSearchActivity::class.java))
        }
    }

    private fun setupPlusToiletButton() {
        val plusToiletButton: LinearLayout = findViewById(R.id.toiletplus_button)
        plusToiletButton.setOnClickListener {
            startActivity(Intent(this, ToiletPlusActivity::class.java))
        }
    }

    private fun setupMyPageButton() {
        val mypageButton: LinearLayout = findViewById(R.id.my_toilet_button)
        mypageButton.setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }
    }

    private fun setupDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout_login)
        val navigationView: NavigationView = findViewById(R.id.nav_view_after_login)

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val layoutParams = navigationView.layoutParams
        layoutParams.width = (screenWidth * 0.3).toInt()
        navigationView.layoutParams = layoutParams

        navigationView.setNavigationItemSelectedListener { menuItem ->

            Log.d("nonlogin", "Menu item clicked: ${menuItem.itemId}")

            when (menuItem.itemId) {
                R.id.nav_mypage -> {
                    Log.d("login NAV",R.id.nav_icon1.toString())
                    startActivity(Intent(this, MyPageActivity::class.java))
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }

        //footer
        val navFooter = findViewById<LinearLayout>(R.id.nav_footer)
        navFooter.setOnClickListener {
            Toast.makeText(this, "Delete Account clicked!", Toast.LENGTH_SHORT).show()

            // 확인 다이얼로그 생성
            val builder = AlertDialog.Builder(this)
            builder.setTitle("계정 탈퇴")
            builder.setMessage("정말로 계정을 탈퇴하시겠습니까?")

            // "탈퇴" 버튼 클릭
            builder.setPositiveButton("탈퇴") { dialog, _ ->
            val googleHelper = GoogleHelper.getInstance(this)

                // 코루틴을 사용하여 계정 탈퇴 처리
                CoroutineScope(Dispatchers.Main).launch {
                    val isDeleted = googleHelper.deleteGoogleAccount()
                    if (isDeleted) {
                        Log.d("MainActivity", "계정 탈퇴 성공")
                        Toast.makeText(this@MainActivity, "계정이 성공적으로 탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Log.e("MainActivity", "계정 탈퇴 실패")
                        Toast.makeText(this@MainActivity, "계정 탈퇴에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }

            // "취소" 버튼 클릭
            builder.setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss() // 다이얼로그 닫기
            }

            // 다이얼로그 표시
            val dialog = builder.create()
            dialog.show()
        }



        val headerView : View = navigationView.getHeaderView(0)
        val navLogoutButton : LinearLayout = headerView.findViewById(R.id.loginbtn)
        navLogoutButton.setOnClickListener {
            googleSignInHelper.signOut()
        }

        setupMenuIcon() // 메뉴 아이콘 클릭 리스너 설정
    }


    private fun setupMenuIcon() {
        findViewById<ImageView>(R.id.menu_icon_login).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun updateHeader() {
        val navigationView: NavigationView = findViewById(R.id.nav_view_after_login)
        val headerView = navigationView.getHeaderView(0)

        val loginImageView = headerView.findViewById<ImageView>(R.id.login_icon)
        val loginTextView = headerView.findViewById<TextView>(R.id.login_text)

        loginImageView.setImageResource(R.drawable.logout)
        loginTextView.text = "로그아웃" // 로그인 상태일 때
    }
}
