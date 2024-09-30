package com.example.disabledtoilet_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.disabledtoilet_android.Near.NearActivity
import com.example.disabledtoilet_android.databinding.ActivityNonloginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class NonloginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNonloginBinding
    private lateinit var googleSignInClient  :GoogleSignInClient
    private lateinit var firebaseAuth : FirebaseAuth

    private val RC_SIGN_IN = 9001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNonloginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //내 주변
        val nearButton : LinearLayout = findViewById(R.id.near_button)

        nearButton.setOnClickListener{
            val intent = Intent(this, NearActivity::class.java)
            startActivity(intent)
        }

        //장소 검색
        val searchButton : LinearLayout = findViewById(R.id.search_button)

        searchButton.setOnClickListener{
            val intent = Intent(this, NearActivity::class.java)
            startActivity(intent)
        }

        //구글 로그인

        //FirebaseAuth 인스턴스 초기화
        firebaseAuth = FirebaseAuth.getInstance()

        //GoogleSignInOptions 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.login_id)) // ID Token 요청
            .requestEmail() // 이메일 요청
            .build()

        // GoogleSignInClient 초기화
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 로그인 버튼 클릭 시 동작
        val googleLoginButton : Button = findViewById(R.id.google_login_button)

        googleLoginButton.setOnClickListener {
            signIn()
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //로그인 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // 로그인 실패 처리
                Log.w("GoogleLogin", "Google sign in failed", e)
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
                } else {
                    // 로그인 실패 처리
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
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