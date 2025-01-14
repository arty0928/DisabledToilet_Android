package com.dream.disabledtoilet_android.User

import com.google.firebase.firestore.FirebaseFirestore

/**
 * 화장실 좋아요 추가 , 삭제 및 firebase 실시간 업데이트
 */
class ToiletPostRepository {
    private val db = FirebaseFirestore.getInstance()

    /**
     * 좋아요 추가
     */
    fun addLike(toiletId: Int, userId: String) {
        val postRef = db.collection("dreamhyoja").document(toiletId.toString())
        db.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val likes = snapshot.get("save") as? MutableList<String> ?: mutableListOf()
            if (!likes.contains(userId)) {
                likes.add(userId)
                transaction.update(postRef, "save", likes)
            }
        }
    }

    /**
     * 좋아요 제거
      */
    fun removeLike(toiletId: Int, userId: String) {
        val postRef = db.collection("dreamhyoja").document(toiletId.toString())
        db.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val likes = snapshot.get("save") as? MutableList<String> ?: mutableListOf()
            if (likes.contains(userId)) {
                likes.remove(userId)
                transaction.update(postRef, "save", likes)
            }
        }
    }

    /**
     * 게시글 좋아요 실시간 업데이트
     */
    fun observePostLikes(toiletId: Int, callback: (List<String>) -> Unit) {
        db.collection("dreamhyoja").document(toiletId.toString())
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val likes = snapshot.get("save") as? List<String> ?: emptyList()
                    callback(likes)
                }
            }
    }
}