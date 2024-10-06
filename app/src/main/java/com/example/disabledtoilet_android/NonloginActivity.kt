package com.example.disabledtoilet_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.disabledtoilet_android.Near.NearActivity
import com.example.disabledtoilet_android.ToiletSearch.ToiletFilterSearchActivity
import com.example.disabledtoilet_android.ToiletSearch.ToiletRepository
import com.example.disabledtoilet_android.databinding.ActivityNonloginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class NonloginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNonloginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var drawerLayout  : DrawerLayout

    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNonloginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        //내 주변
        val nearButton: LinearLayout = findViewById(R.id.near_button)
        nearButton.setOnClickListener {
            val intent = Intent(this, NearActivity::class.java)
            startActivity(intent)
        }

        //장소 검색
        val searchButton: LinearLayout = findViewById(R.id.search_button)
        searchButton.setOnClickListener {
            val intent = Intent(this, NearActivity::class.java)
            startActivity(intent)
        }

        // 구글 로그인 초기화
        firebaseAuth = FirebaseAuth.getInstance()

        // 로그인 버튼 클릭 시 동작
        val googleLoginButton: Button = findViewById(R.id.google_login_button)
        googleLoginButton.setOnClickListener {
            startLoginGoogle()
        }

        //Navigation DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout_login)
        //NaviationView 설정
        val navigationView : NavigationView = findViewById(R.id.nav_view)

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

        //menu_icon 클릭시 Drawer 열기
        val menuIcon : ImageView = findViewById(R.id.menu_icon)
        menuIcon.setOnClickListener {
//            drawerLayout.openDrawer(navigationView)
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun startLoginGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id)) // 이 부분을 확인
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
        googleLoginResult.launch(googleSignInClient.signInIntent)
    }

    private val googleLoginResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data

            try {
                val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = completedTask.getResult(ApiException::class.java)
                onLoginCompleted("${account?.id}", "${account?.idToken}")
            } catch (e: ApiException) {
                // 상세한 에러 로그 추가
                Log.e(TAG, "Google sign in failed with status code: ${e.statusCode}")
                Log.e(TAG, "Detailed message: ${e.message}")
                Log.e(TAG, "Exception stacktrace:", e)
                onError(Error(e))
            }
        }

    private fun onLoginCompleted(userId: String?, accessToken: String?) {
        Toast.makeText(this, "구글 로그인 성공", Toast.LENGTH_SHORT).show()
        Log.e(TAG, "userId: $userId / accessToken: $accessToken")

        // MainActivity로 이동
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) // 이전 액티비티 제거
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }

    private fun onError(error: Error?) {
        Toast.makeText(this, "구글 로그인 실패", Toast.LENGTH_SHORT).show()
        Log.e(TAG, "구글 로그인 실패 onError / error: ${error} / error.msg: ${error?.message}")
    }

    // 로그인 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // 로그인 성공
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // 로그인 실패 처리
                Log.e(TAG, "Google sign in failed with status code: ${e.statusCode}")
                Log.e(TAG, "Google sign in failed with message: ${e.message}")
                Log.e(TAG, "Exception stacktrace:", e)
                Toast.makeText(this, "로그인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Firebase로 Google 로그인 인증
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공 처리
                    val user = firebaseAuth.currentUser
                    Log.d(TAG, "signInWithCredential:success")
                    updateUI(user)
                } else {
                    // 로그인 실패 처리
                    Log.e(TAG, "signInWithCredential:failure", task.exception)
                    Log.e(TAG, "Firebase sign-in failed with message: ${task.exception?.message}")
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // 로그인 성공 시 UI 업데이트
            Toast.makeText(this, "로그인 성공: ${user.displayName}", Toast.LENGTH_SHORT).show()
        } else {
            // 로그인 실패 시 UI 업데이트
            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "Login"
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        // 로그인된 유저가 있으면 처리
    }

    // 로그아웃 함수
    private fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {
            // 로그아웃 완료 후 처리
        }
    }
}
