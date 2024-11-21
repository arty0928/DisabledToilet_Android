package com.example.disabledtoilet_android.Utility.Dialog.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.lifecycleScope
import com.example.disabledtoilet_android.MainActivity
import com.example.disabledtoilet_android.NonloginActivity
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.ToiletSearch.ToiletData
import com.example.disabledtoilet_android.User.User
import com.example.disabledtoilet_android.User.getLoggedInUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class GoogleHelper private constructor(private val context: Context) {

    private val job = Job()

    companion object {
        @Volatile
        private var INSTANCE: GoogleHelper? = null

        fun getInstance(context: Context): GoogleHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GoogleHelper(context).also { INSTANCE = it }
            }
        }
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "GoogleHelper"

    // SharedPreferences 키
    private val PREFS_NAME = "UserPrefs"
    private val KEY_IS_LOGGED_IN = "isLoggedIn"
    private val KEY_USER_EMAIL = "userEmail"

    /**
     * 구글 로그인 클라이언트 초기화
     */
    suspend fun initializeGoogleSignIn(): Boolean {
        val isSuccess = CompletableDeferred<Boolean>()

        withContext(Dispatchers.IO) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(context, gso)

            if (googleSignInClient == null) {
                isSuccess.complete(false)
                throw Exception("GoogleSignInClient initialization failed")
            }
            
            //로그인한 상태인지 확인후, 사용자 정보 업데이트
            val success = fetchUserData()

            isSuccess.complete(success)

        }
        return isSuccess.await()
    }

    /**
     * 구글 로그인 처리 후, 사용자 정보 업데이트
     */
    suspend fun fetchUserData(): Boolean {
        val email = getUserEmail()

        Log.d(TAG, email.toString())
        
        //로그인된 사용자였으면 해당 사용자 정보 firebase에서 가져오기
        return if (email != null) {
            // Firebase에서 사용자 데이터 가져오기
            val deferred = CompletableDeferred<Boolean>()


            getLoggedInUser(email) { success ->
                Log.d("GoogleHelper fetch", success.toString())
                if (success) {
                    deferred.complete(true)
                } else {
                    deferred.complete(false) // 사용자 데이터 없거나 오류 발생
                }
            }
            deferred.await() // 데이터를 받을 때까지 기다림
        } else {
            false
        }
    }

    /**
     * 구글 로그인 시작
     */
    fun startLoginGoogle(activityResultLauncher: ActivityResultLauncher<Intent>) {
        activityResultLauncher.launch(googleSignInClient.signInIntent)
    }

    /**
     * 구글 로그인 결과 처리
     */
    fun handleSignInResult(data: Intent?, onSuccess: (GoogleSignInAccount) -> Unit) {
        try {
            val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                onSuccess(account)
                saveLoginState(account.email!!) // 로그인 성공 시 상태 저장
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign in failed: ${e.message}", e)
        }
    }

    /**
     * Firestore에서 사용자 존재 여부 확인
     */
    fun checkUserInFirestore(account: GoogleSignInAccount) {
        val db = FirebaseFirestore.getInstance()
        val userDoc = db.collection("users").document(account.id!!)

        userDoc.get().addOnSuccessListener { document ->
            if (document.exists()) {
                onLoginCompleted(account.id, account.idToken)
            } else {
                saveUserToFirestore(account)
                onLoginCompleted(account.id, account.idToken)
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error checking user in Firestore", e)
        }
    }

    /**
     * Firestore에 사용자 데이터 저장
     */
    private fun saveUserToFirestore(account: GoogleSignInAccount) {
        val userData = hashMapOf(
            "name" to account.displayName,
            "email" to account.email,
            "photoURL" to account.photoUrl.toString(),
            "likedToilets" to listOf<String>(),
            "recentlyViewedToilets" to listOf<String>(),
            "registeredToilets" to listOf<String>()
        )

        val db = FirebaseFirestore.getInstance()
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
     * 로그인 완료 후 메인 화면으로 이동
     */
    private fun onLoginCompleted(userId: String?, accessToken: String?) {
        Log.d(TAG, "구글 로그인 성공")
        Toast.makeText(context, "구글 로그인 성공", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * 구글 로그아웃
     */
    fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

            //UserRepository에 저장된 currentUser 초기화
            ToiletData.clearCurrentUser()
            saveLoginState(null) // 로그아웃 시 상태 초기화

            context.startActivity(Intent(context, NonloginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
        }
    }

    /**
     * 로그인 상태 저장
     * @param userEmail 사용자의 이메일 (로그인 시 저장, 로그아웃 시 null로 설정)
     */
    private fun saveLoginState(userEmail: String?) {
        Log.d(TAG, "saveLoginState , ${userEmail}")
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(KEY_IS_LOGGED_IN, userEmail != null) // 로그인 상태를 boolean 값으로 저장
            putString(KEY_USER_EMAIL, userEmail) // 이메일 정보 저장 (로그아웃 시 null)
            apply()
        }
    }

    /**
     * 저장된 사용자 이메일 정보 확인
     * @return 사용자 이메일 (로그인 상태일 경우)
     */
    fun getUserEmail(): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

}