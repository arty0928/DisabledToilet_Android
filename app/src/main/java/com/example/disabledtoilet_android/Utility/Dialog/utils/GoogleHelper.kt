package com.example.disabledtoilet_android.Utility.Dialog.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.example.disabledtoilet_android.MainActivity
import com.example.disabledtoilet_android.NonloginActivity
import com.example.disabledtoilet_android.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GoogleHelper private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: GoogleHelper? = null

        fun getInstance(context: Context): GoogleHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GoogleHelper(context).also { INSTANCE = it }
            }
        }
    }

    lateinit var googleSignInClient: GoogleSignInClient
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "GoogleHelper"

    suspend fun initializeGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)

        if (googleSignInClient == null) {
            throw Exception("GoogleSignInClient initialization failed")
        }
    }

    fun startLoginGoogle(activityResultLauncher: ActivityResultLauncher<Intent>) {
        activityResultLauncher.launch(googleSignInClient.signInIntent)
    }

    fun handleSignInResult(data: Intent?, onSuccess: (GoogleSignInAccount) -> Unit) {
        try {
            val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                onSuccess(account)
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign in failed: ${e.message}", e)
        }
    }

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

    private fun saveUserToFirestore(account: GoogleSignInAccount) {
        val userData = hashMapOf(
            "name" to account.displayName,
            "email" to account.email,
            "photoURL" to account.photoUrl.toString(),
            "likedToilets" to listOf<String>(),
            "recentlyViewedToilets" to listOf<String>()
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

    private fun onLoginCompleted(userId: String?, accessToken: String?) {
        Toast.makeText(context, "구글 로그인 성공", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }

    fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, NonloginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
        }
    }
}
