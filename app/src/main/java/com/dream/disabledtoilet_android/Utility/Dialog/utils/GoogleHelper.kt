package com.dream.disabledtoilet_android.Utility.Dialog.utils

import User
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dream.disabledtoilet_android.BuildConfig
import com.dream.disabledtoilet_android.MainActivity
import com.dream.disabledtoilet_android.NonloginActivity
import com.dream.disabledtoilet_android.ToiletSearch.ToiletData
import com.dream.disabledtoilet_android.User.ViewModel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class GoogleHelper private constructor(private val context: Context) {

    private val job = Job()
    private val firestore = FirebaseFirestore.getInstance()

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
            Log.d("test es google 1", "google initialzie")
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.WEB_CLIENT_ID)
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(context, gso)

            if (googleSignInClient == null) {
                isSuccess.complete(false)
                throw Exception("GoogleSignInClient initialization failed")
            }

            // 로그인한 상태인지 확인 후, 사용자 정보 업데이트
            val success = fetchUserData()

            isSuccess.complete(success)
        }
        return isSuccess.await()
    }


    /**
     * 구글 로그인 처리 후, 사용자 정보 업데이트
     */
    suspend fun fetchUserData(): Boolean {
        val email = getUserEmail() // 사용자의 이메일 가져오기

        if (email == null) {
            Log.e("test es", "사용자 이메일이 null입니다.")
            return false
        }

        val isSuccess = CompletableDeferred<Boolean>()

        try {
            firestore.collection("users")
                .document(email)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Firestore 데이터를 Map으로 가져오기
                        val data = document.data

                        if (data != null) {
                            // Map 데이터를 User 객체로 변환
                            val user = User(
                                email = data["email"] as? String ?: "",
                                name = data["name"] as? String ?: "",
                                photoURL = data["photoURL"] as? String ?: "",
                                likedToilets = data["likedToilets"] as? List<Int> ?: emptyList(),
                                recentlyViewedToilets = MutableLiveData(
                                    (data["recentlyViewedToilets"] as? List<Int>)?.toMutableList() ?: mutableListOf()
                                ),
                                registedToilets = MutableLiveData(
                                    (data["registedToilets"] as? List<Int>)?.toMutableList() ?: mutableListOf()
                                )
                            )

                            ToiletData.currentUser = user
                            isSuccess.complete(true)
                        } else {
                            Log.e("test es", "Firestore 문서에 데이터가 없습니다.")
                            isSuccess.complete(false)
                        }
                    } else {
                        Log.e("test es", "Firestore 문서가 존재하지 않습니다.")
                        isSuccess.complete(false)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("test es", "Firestore 데이터 가져오기 실패", e)
                    isSuccess.complete(false)
                }

        } catch (e: Exception) {
            Log.e("test es", "예외 발생: ${e.message}", e)
            isSuccess.complete(false)
        }

        return isSuccess.await()
    }



    /**
     * 구글 로그인 시작
     */
    fun startLoginGoogle(activityResultLauncher: ActivityResultLauncher<Intent>) {
        Log.d(TAG, "startLoginGoogle")
        activityResultLauncher.launch(googleSignInClient.signInIntent)
    }

    /**
     * 구글 로그인 결과 처리
     */
    fun handleSignInResult(data: Intent?, onSuccess: (GoogleSignInAccount) -> Unit) {
        try {
            Log.d(TAG + "handleSignInResult", BuildConfig.WEB_CLIENT_ID)

            val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                // Firebase 인증을 통해 로그인된 사용자로 설정
                firebaseAuthWithGoogle(account)
                onSuccess(account)
                saveLoginState(account.email!!) // 로그인 성공 시 상태 저장
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign in failed: ${e.message}", e)
        }
    }

    /**
     * Firebase에 Google 인증 정보 전달
     * @param account GoogleSignInAccount
     */
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 로그인 성공 시, firebaseAuth.currentUser를 로그인한 사용자로 설정
                    val user = firebaseAuth.currentUser
                    Log.d(TAG, "Firebase authentication successful, user: ${user?.email}")
                } else {
                    Log.e(TAG, "Firebase authentication failed", task.exception)
                }
            }
    }


    /**
     * Firestore에서 사용자 존재 여부 확인
     */
    fun checkUserInFirestore(account: GoogleSignInAccount) {
        val db = FirebaseFirestore.getInstance()
        val userDoc = db.collection("users").document(account.email!!)

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
        db.collection("users").document(account.email!!)
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

    /**
     * Google 계정 탈퇴 처리
     */
    suspend fun deleteGoogleAccount(): Boolean {
        val user = firebaseAuth.currentUser
        val email = getUserEmail()

        Log.d(TAG + " user", user.toString())

        if ( user == null || email == null) {
            Log.e(TAG, "사용자가 로그인되어 있지 않습니다.")
            return false
        }

        return withContext(Dispatchers.IO) {
            try {
                // Firestore에서 사용자 데이터 삭제
                val db = FirebaseFirestore.getInstance()
                val userDoc = db.collection("users").document(email)

                Log.d(TAG + "1", userDoc.toString())

                userDoc.delete().addOnSuccessListener {
                    Log.d(TAG + "2", "Firestore에서 사용자 데이터 삭제 성공")
                }.addOnFailureListener { e ->
                    Log.e(TAG + "3", "Firestore에서 사용자 데이터 삭제 실패", e)
                }

                // FirebaseAuth에서 사용자 삭제
                val deletionSuccess = CompletableDeferred<Boolean>()

                user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG + "4", "Firebase Auth에서 사용자 삭제 성공")
                        deletionSuccess.complete(true)
                    } else {
                        Log.e(TAG + "5", "Firebase Auth에서 사용자 삭제 실패", task.exception)
                        deletionSuccess.complete(false)
                    }
                }

                val result = deletionSuccess.await()
                if (result) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    signOut() // 로그아웃 처리 (초기화, NonLogin 으로 다시 이동)
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "회원 탈퇴에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
                result
            } catch (e: Exception) {
                Log.e(TAG + "6", "Google 계정 탈퇴 처리 중 예외 발생", e)
                false
            }
        }
    }

}