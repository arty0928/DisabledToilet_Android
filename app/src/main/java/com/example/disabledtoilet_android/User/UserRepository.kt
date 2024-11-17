package com.example.disabledtoilet_android.User

import ToiletModel
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay


/**
 * 사용자 데이터 저장
 */
class UserRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * 저장할 사용자 데이터 변수
     */

    var currentUser : User ? = null
        private set //외부에서 설정할 수 없도록 private set

    /**
     * 로그인한 사용자 정보를 가져와서, 현재 사용자 정보 currentUser 업데이트
     * @param email 사용자의 이메일
     * @param callback 사용자 정보를 처리할 콜백
     */
    fun getLoggedInUser(email: String, callback: (Boolean) -> Unit) {
        // Firestore에서 사용자 데이터 가져오기
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val name = document.getString("name") ?: ""
                    val photoURL = document.getString("photoURL") ?: ""
                    val likedToilets =
                        document.get("likedToilets") as? List<ToiletModel> ?: listOf()
                    val recentlyViewedToilets =
                        document.get("recentlyViewedToilets") as? List<ToiletModel> ?: listOf()

                    // User 객체 생성
                    val user = User(
                        email = email,
                        name = name,
                        photoURL = photoURL,
                        likedToilets = likedToilets,
                        recentlyViewedToilets = recentlyViewedToilets
                    )

                    // currentUser에 저장
                    currentUser = user
                    Log.d("GoogleHelper" , currentUser.toString())
                    callback(true)
                } else {
                    callback(false) // 문서가 없음
                }
            }
            .addOnFailureListener {
                callback(false) // 오류 발생
            }
    }

    suspend fun getCurretnUser() : User?{
        delay(1000)
        return currentUser
    }

    /**
     * 로그아웃 -> 저장된 사용자 데이터 삭제
     */
    fun clearCurrentUser(){
        currentUser = null
    }
}