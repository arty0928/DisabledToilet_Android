package com.example.disabledtoilet_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.disabledtoilet_android.Near.NearActivity
import com.example.disabledtoilet_android.ToiletPlus.ToiletPlusActivity
import com.example.disabledtoilet_android.ToiletSearch.ToiletData
import com.example.disabledtoilet_android.ToiletSearch.ToiletFilterSearchActivity
import com.example.disabledtoilet_android.databinding.ActivityNonloginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.disabledtoilet_android.Utility.Dialog.dialog.LoadingDialog
import com.kakao.sdk.common.KakaoSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.gms.auth.api.signin.GoogleSignInAccount


class NonloginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNonloginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    private var loadingDialog = LoadingDialog()

    companion object {
        private const val TAG = "Login"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoSdk.init(this, "ce27585c8cc7c468ac7c46901d87199d")

        firebaseAuth = FirebaseAuth.getInstance()
        binding = ActivityNonloginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeUI()
        loadToiletData()
    }

    /**
     * UI 요소를 초기화하고 클릭 리스너를 설정합니다.
     */
    private fun initializeUI() {
        loadingDialog.show(supportFragmentManager, loadingDialog.tag)

        // 내 주변 버튼 클릭 리스너
        binding.nearButton.setOnClickListener {
            startActivity(Intent(this, NearActivity::class.java))
        }

        // 장소 검색 버튼 클릭 리스너
        binding.searchButton.setOnClickListener {
            startActivity(Intent(this, ToiletFilterSearchActivity::class.java))
        }

        setupNavigationDrawer()
        updateNavHeader()
    }

    /**
     * Navigation Drawer를 설정하고 관련 리스너를 초기화합니다.
     */
    private fun setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout_nonlogin)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        // 내비게이션 뷰 너비 설정
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        navigationView.layoutParams.width = (screenWidth * 0.3).toInt()

        // NavigationView 아이템 클릭 리스너
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_plusItem -> {
                    Toast.makeText(this, "Plus Item 클릭됨", Toast.LENGTH_SHORT).show()
                    startLoginGoogle()
                    startActivity(Intent(this, ToiletPlusActivity::class.java))
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }

        // 메뉴 아이콘 클릭 시 드로어 열기
        findViewById<ImageView>(R.id.menu_icon).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 로그인/로그아웃 처리
        val headerView: View = navigationView.getHeaderView(0)
        headerView.findViewById<LinearLayout>(R.id.login_nav_login_button).setOnClickListener {
            if (firebaseAuth.currentUser != null) {
                signOut()
            } else {
                startLoginGoogle()
            }
        }

        // Navigation Drawer 메뉴 아이템 설정
        setUpNavigationMenuItems(navigationView)
    }

    /**
     * 화장실 데이터를 비동기로 로드합니다.
     */
    private fun loadToiletData() {
        CoroutineScope(Dispatchers.IO).launch {
            val initResult = ToiletData.initialize()
            withContext(Dispatchers.Main) {
                loadingDialog.dismiss()
                if (initResult) {
                    Log.d(TAG, "Toilet data loaded successfully.")
                } else {
                    Log.e(TAG, "Failed to load toilet data.")
                }
            }
        }
    }

    /**
     * 내비게이션 헤더를 업데이트하여 로그인 상태에 따라 UI를 변경합니다.
     */
    private fun updateNavHeader() {
        val currentUser = firebaseAuth.currentUser
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val loginIcon: ImageView = headerView.findViewById(R.id.login_icon)
        val loginIconText: TextView = headerView.findViewById(R.id.login_text)

        if (currentUser != null) {
            loginIcon.setImageResource(R.drawable.logout)
            loginIconText.text = "로그아웃"
        } else {
            loginIcon.setImageResource(R.drawable.login)
            loginIconText.text = "로그인"
        }
    }

    /**
     * Navigation Drawer의 메뉴 아이템을 설정합니다.
     */
    private fun setUpNavigationMenuItems(navigationView: NavigationView) {
        val menu = navigationView.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            val actionView = menuItem.actionView
            val menuItemText = actionView?.findViewById<TextView>(R.id.nav_plusItem_txt)
            val menuItemIcon = actionView?.findViewById<ImageView>(R.id.nav_plusitem_icon)

            when (i) {
                0 -> {
                    menuItemText?.text = "화장실 등록"
                    menuItemIcon?.setImageResource(R.drawable.plustoilet_main)
                }
            }
        }
    }

    /**
     * 구글 로그인을 시작합니다.
     */
    private fun startLoginGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
        googleLoginResult.launch(googleSignInClient.signInIntent)
    }

    /**
     * 구글 로그인 결과를 처리합니다.
     */
    private val googleLoginResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data
            try {
                val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = completedTask.getResult(ApiException::class.java)
                if (account != null) {
                    checkUserInFirestore(account)
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign in failed: ${e.message}", e)
            }
        }

    /**
     * Firestore에서 사용자의 존재 여부를 확인하고, 없으면 추가합니다.
     *
     * @param account GoogleSignInAccount - 구글 로그인 계정 정보
     */
    private fun checkUserInFirestore(account: GoogleSignInAccount) {
        val db = FirebaseFirestore.getInstance()
        val userDoc = db.collection("users").document(account.id!!)

        userDoc.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // 이미 등록된 사용자일 경우
                onLoginCompleted(account.id, account.idToken)
            } else {
                // 새로운 사용자일 경우
                saveUserToFirestore(account)
                onLoginCompleted(account.id, account.idToken)
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error checking user in Firestore", e)
        }
    }

    /**
     * 구글 로그인 계정 정보를 Firestore에 저장합니다.
     *
     * @param account GoogleSignInAccount - 구글 로그인 계정 정보
     */
    private fun saveUserToFirestore(account: GoogleSignInAccount) {
        // 사용자 정보를 저장할 해시맵 생성
        val userData = hashMapOf(
            "name" to account.displayName,
            "email" to account.email,
            "photoURL" to account.photoUrl.toString(),
            "likedToilets" to listOf<String>(), // 좋아요한 화장실 리스트
            "recentlyViewedToilets" to listOf<String>() // 최근 본 화장실 리스트
        )

        // Firestore 인스턴스 가져오기
        val db = FirebaseFirestore.getInstance()

        // 사용자 정보를 "users" 컬렉션에 저장
        db.collection("users").document(account.id!!)
            .set(userData)
            .addOnSuccessListener {
                Log.d(TAG, "User data saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving user data", e)
            }
    }


    /**
     * 로그인 성공 시 호출되어 메인 액티비티로 전환합니다.
     */
    private fun onLoginCompleted(userId: String?, accessToken: String?) {
        Toast.makeText(this, "구글 로그인 성공", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        })
        finish()
    }

    /**
     * 사용자를 로그아웃 처리합니다.
     */
    private fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {
            updateNavHeader()
        }
    }
}
