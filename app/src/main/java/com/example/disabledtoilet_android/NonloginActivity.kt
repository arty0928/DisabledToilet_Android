package com.example.disabledtoilet_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.disabledtoilet_android.Near.NearActivity
import com.example.disabledtoilet_android.ToiletPlus.ToiletPlusActivity
import com.example.disabledtoilet_android.ToiletSearch.ToiletFilterSearchActivity
import com.example.disabledtoilet_android.databinding.ActivityNonloginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NonloginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNonloginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout

    companion object {
        private const val TAG = "NonloginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNonloginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance() // Firebase 인증 인스턴스 초기화
        initializeUI() // UI 초기화
    }

    private fun initializeUI() {
        // UI 요소에 클릭 리스너 설정
        binding.nearButton.setOnClickListener {
            startActivity(Intent(this, NearActivity::class.java)) // Nearby Activity로 이동
        }

        binding.searchButton.setOnClickListener {
            startActivity(Intent(this, ToiletFilterSearchActivity::class.java)) // Toilet Filter Search Activity로 이동
        }

        setupNavigationDrawer() // 내비게이션 드로어 설정
    }

    private fun setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout_nonlogin) // 드로어 레이아웃 초기화
        val navigationView: NavigationView = findViewById(R.id.nav_view_before_login)

        // 내비게이션 아이템 클릭 리스너 설정
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_plusItem -> {
                    startLoginGoogle() // 구글 로그인 시작
                    true
                }
                else -> false
            }
        }

        // 메뉴 아이콘 클릭 시 드로어 열기
        findViewById<ImageView>(R.id.menu_icon).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun startLoginGoogle() {
        // 구글 로그인 옵션 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(applicationContext, gso) // 구글 로그인 클라이언트 초기화
        googleLoginResult.launch(googleSignInClient.signInIntent) // 로그인 인텐트 시작
    }

    /**
     * 구글 로그인 결과를 처리합니다.
     */
    private val googleLoginResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
}
