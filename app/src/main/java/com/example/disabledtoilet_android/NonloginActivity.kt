package com.example.disabledtoilet_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.example.disabledtoilet_android.Near.NearActivity
import com.example.disabledtoilet_android.ToiletPlus.ToiletPlusActivity
import com.example.disabledtoilet_android.ToiletSearch.ToiletData
import com.example.disabledtoilet_android.ToiletSearch.ToiletFilterSearchActivity
import com.example.disabledtoilet_android.User.MyPageActivity
import com.example.disabledtoilet_android.databinding.ActivityNonloginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.example.disabledtoilet_android.ToiletSearch.ToiletRepository
import com.example.disabledtoilet_android.Utility.Dialog.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NonloginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNonloginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    var loadingDialog = LoadingDialog()

    private val RC_SIGN_IN = 9001

    fun updateNavHeader() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        binding = ActivityNonloginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Log.d("MyApplication", "Initializing ToiletRepository...")

        CoroutineScope(Dispatchers.Main).launch {
            loadingDialog.show(supportFragmentManager, loadingDialog.tag)

            ToiletData.initialize { success ->
                // 데이터 로드 완료 후 loadingDialog.dismiss() 호출
                loadingDialog.dismiss()

                if (success) {
                    Log.d(TAG, "Toilet data loaded successfully.")
                    // 추가적인 성공 처리 로직을 여기에 작성할 수 있습니다.
                } else {
                    Log.e(TAG, "Failed to load toilet data.")
                    // 실패 처리 로직을 여기에 작성할 수 있습니다.
                }
            }
        }


        // 내 주변 버튼
        val nearButton = binding.nearButton
        nearButton.setOnClickListener {
            val intent = Intent(this, NearActivity::class.java)
            startActivity(intent)
        }

        // 장소 검색 버튼
        val searchButton = binding.searchButton
        searchButton.setOnClickListener {
            val intent = Intent(this, ToiletFilterSearchActivity::class.java)
            startActivity(intent)
        }

        drawerLayout = findViewById(R.id.drawer_layout_nonlogin)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        //NavigationView 너비를 화면의 80%로 설정
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val layoutParams = navigationView.layoutParams
        layoutParams.width =  (screenWidth * 0.3).toInt()
        navigationView.layoutParams = layoutParams

        updateNavHeader()

        //drawer navigation custom item

        navigationView.setNavigationItemSelectedListener { menuItem ->
            val handled = when (menuItem.itemId) {
                R.id.nav_plusItem -> {
                    Toast.makeText(this, "Plus Item 클릭됨", Toast.LENGTH_SHORT).show()
                    startLoginGoogle()

                    val intent = Intent(this, ToiletPlusActivity::class.java)  // MyPageActivity로 이동
                    startActivity(intent)
                    drawerLayout.closeDrawers()  // Drawer 닫기
                    true

                }
                else -> false
            }
            drawerLayout.closeDrawers()
            handled
        }


        // menu_icon 클릭시 Drawer 열기
        val menuIcon: ImageView = findViewById(R.id.menu_icon)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 로그인/로그아웃 처리
        val headerView: View = navigationView.getHeaderView(0)
        val navLoginButton: LinearLayout = headerView.findViewById(R.id.login_nav_login_button)
        navLoginButton.setOnClickListener {
            if (firebaseAuth.currentUser != null) {
                signOut()
            } else {
                startLoginGoogle()
            }
        }

        //Navigatio Drawer 메뉴 아이템 설정
        setUpNavigationMenuItems(navigationView)

    }

    private fun setUpNavigationMenuItems(navigationView: NavigationView){
        val menu = navigationView.menu
        for(i in 0 until menu.size()){
            val menuItem = menu.getItem(i)
            val actionView = menuItem.actionView
            val menuItemText = actionView?.findViewById<TextView>(R.id.nav_plusItem_txt)
            val menuItemIcon = actionView?.findViewById<ImageView>(R.id.nav_plusitem_icon)

            when(i) {
                0 -> {
                    menuItemText?.text = "화장실 등록"
                    menuItemIcon?.setImageResource(R.drawable.plustoilet_main)

                }
            }
        }
    }

    private fun startLoginGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
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
                Log.e(TAG, "Google sign in failed: ${e.message}", e)
            }
        }

    private fun onLoginCompleted(userId: String?, accessToken: String?) {
        Toast.makeText(this, "구글 로그인 성공", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {
            updateNavHeader()
        }
    }

    companion object {
        private const val TAG = "Login"
    }
}
