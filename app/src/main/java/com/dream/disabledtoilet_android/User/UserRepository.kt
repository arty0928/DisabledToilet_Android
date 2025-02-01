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
    fun loadUser(email: String, callback: (User?) -> Unit) {
        Log.d("UserRepository", "Loading user with email: $email")
        db.collection("users")
            .document(email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.exists()) {
                    val user = documents.toObject(User::class.java)
                    Log.d("UserRepository", "User loaded: $user")
                    callback(user)
                } else {
                    Log.d("UserRepository", "User document does not exist")
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UserRepository", "Failed to load user: ${exception.message}")
                callback(null)
            }
    }


    /**
     * Firebase에 사용자 데이터를 업데이트
     * @param email 사용자 이메일
     * @param updatedUser 업데이트할 User 데이터
     */
    fun uploadFirebase(email: String, updatedUser: User) {
        val userMap = hashMapOf(
            "email" to updatedUser.email,
            "name" to updatedUser.name,
            "photoURL" to updatedUser.photoURL,
            "likedToilets" to updatedUser.likedToilets.map { it.toString() },
            "registedToilets" to updatedUser.registedToilets
        )

        db.collection("users")
            .document(email)
            .set(userMap)
            .addOnSuccessListener {
                Log.d("UserRepository", "User data successfully uploaded to Firebase.")
            }
            .addOnFailureListener { exception ->
                Log.e("UserRepository", "Failed to upload user data: ${exception.message}")
            }
    }

    /**
     * 좋아요 추가 또는 삭제
     * @param toiletId 화장실 ID
     * @param userEmail 사용자 이메일
     * @return true: 좋아요 추가, false: 좋아요 삭제
     */
    fun toggleLike(toiletId: Int, userEmail: String, callback: (Boolean) -> Unit) {
        val userRef = db.collection("users").document(userEmail)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val likes = snapshot.get("likedToilets") as? MutableList<String> ?: mutableListOf()

            // toiletId를 String으로 변환하여 contains 체크
            if (!likes.contains(toiletId.toString())) {
                // 좋아요 추가
                likes.add(toiletId.toString())
                transaction.update(userRef, "likedToilets", likes)
                return@runTransaction true // 추가했으므로 true 반환
            } else {
                // 좋아요 삭제
                likes.remove(toiletId.toString())
                transaction.update(userRef, "likedToilets", likes)
                return@runTransaction false // 삭제했으므로 false 반환
            }
        }.addOnSuccessListener { result ->
            callback(result) // 결과를 콜백으로 전달
        }.addOnFailureListener { exception ->
            Log.e("UserRepository", "Failed to toggle like: ${exception.message}")
            callback(false) // 실패 시 false 반환
        }
    }


//    /**
//     * 좋아요 추가
//     */
//    fun addLike(toiletId: Int, userId: String) {
//        val postRef = db.collection("users").document(userId)
//        db.runTransaction { transaction ->
//            val snapshot = transaction.get(postRef)
//            val likes = snapshot.get("likedToilets") as? MutableList<String> ?: mutableListOf()
//
//            // toiletId를 String으로 변환하여 contains 체크
//            if (!likes.contains(toiletId.toString())) {
//                likes.add(toiletId.toString())
//                transaction.update(postRef, "likedToilets", likes)
//            }
//        }.addOnFailureListener { exception ->
//            Log.e("UserRepository", "Failed to add like: ${exception.message}")
//        }
//    }
//
//    /**
//     * 좋아요 삭제
//     */
//    fun removeLike(toiletId: Int, userId: String) {
//        val postRef = db.collection("users").document(userId)
//        db.runTransaction { transaction ->
//            val snapshot = transaction.get(postRef)
//            val likes = snapshot.get("likedToilets") as? MutableList<String> ?: mutableListOf()
//
//            // toiletId를 String으로 변환하여 contains 체크
//            if (likes.contains(toiletId.toString())) {
//                likes.remove(toiletId.toString())
//                transaction.update(postRef, "likedToilets", likes)
//            }
//        }.addOnFailureListener { exception ->
//            Log.e("UserRepository", "Failed to remove like: ${exception.message}")
//        }
//    }


}
