package com.example.disabledtoilet_android.User

import ToiletModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class User(

    val email : String,
    val name : String,
    val photoURL : String,

    val likedToilets : List<ToiletModel>,
    val recentlyViewedToilets : List<ToiletModel>

)

// 사용자 데이터를 가져오는 클래스
class UserRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // 로그인한 사용자 정보를 가져오는 메소드
    fun getLoggedInUser(callback: (User?) -> Unit) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Firestore에서 사용자 데이터 가져오기
            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val email = document.getString("email") ?: ""
                        val name = document.getString("name") ?: ""
                        val photoURL = document.getString("photoURL") ?: ""
                        val likedToilets = document.get("likedToilets") as? List<ToiletModel> ?: listOf()
                        val recentlyViewedToilets = document.get("recentlyViewedToilets") as? List<ToiletModel> ?: listOf()

                        val user = User(
                            email = email,
                            name = name,
                            photoURL = photoURL,
                            likedToilets = likedToilets,
                            recentlyViewedToilets = recentlyViewedToilets
                        )
                        callback(user)
                    } else {
                        callback(null) // 문서가 없음
                    }
                }
                .addOnFailureListener {
                    callback(null) // 오류 발생
                }
        } else {
            callback(null) // 로그인된 사용자가 없음
        }
    }
}
