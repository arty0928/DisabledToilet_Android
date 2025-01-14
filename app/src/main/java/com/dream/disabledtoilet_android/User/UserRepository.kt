package com.dream.disabledtoilet_android.User

import User
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Firebase에서 사용자 데이터를 가져와 User 데이터 형식으로 반환
     * @param email 사용자 이메일
     * @return User 객체 (null 가능)
     */
    fun loadUser(email: String, callback : (User?) -> Unit){
        db.collection("users")
            .document(email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.exists()) {
                    val user = documents.toObject(User::class.java)
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

//    /**
//     * Firebase에 사용자 데이터를 업데이트
//     * @param email 사용자 이메일
//     * @param updatedUser 업데이트할 User 데이터
//     */
//    fun uploadFirebase(email: String, updatedUser: User) {
//        val userMap = hashMapOf(
//            "email" to updatedUser.email,
//            "name" to updatedUser.name,
//            "name" to updatedUser.name,
//            "photoURL" to updatedUser.photoURL,
//            "likedToilets" to updatedUser.likedToilets,
//        )
//
//        db.collection("users")
//            .document(email)
//            .set(userMap)
//            .addOnSuccessListener {
//                Log.d("UserRepository", "User data successfully uploaded to Firebase.")
//            }
//            .addOnFailureListener { exception ->
//                Log.e("UserRepository", "Failed to upload user data: ${exception.message}")
//            }
//    }
}
