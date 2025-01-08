package com.dream.disabledtoilet_android.User.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dream.disabledtoilet_android.ToiletSearch.ToiletData
import com.dream.disabledtoilet_android.User.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.Callback
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // 사용자 데이터 LiveData
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    // 특정 화장실 좋아요 상태 LiveData
    private val _likedToilets = MutableLiveData<MutableSet<Int>>()
    val likedToilets: LiveData<MutableSet<Int>> get() = _likedToilets

    /**
     * 사용자 데이터를 이메일을 통해 Firebase에서 로드
     * ToiletData에 저장
     */

    suspend fun loadUser(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Firestore에서 사용자 데이터 로드
                val result = suspendCoroutine<Boolean> { continuation ->
                    firestore.collection("users")
                        .document(email)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                val user = document.toObject(User::class.java)

                                user?.let {
                                    _user.value = it
                                    _likedToilets.value =
                                        (it.likedToilets?.toSet() ?: setOf()) as MutableSet<Int>?

                                    // email로 받아온 사용자 데이터 저장
                                    ToiletData.currentUser = it

                                    Log.d("test es", "User loaded: ${it.email}")
                                    Log.d("test es", "Liked toilets loaded: ${_likedToilets.value}")
                                    continuation.resume(true)
                                } ?: continuation.resume(false)
                            } else {
                                Log.e("test es", "Document does not exist")
                                continuation.resume(false)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("test es", "Error loading user: ${e.message}")
                            continuation.resume(false)
                        }
                }
                return@withContext result
            } catch (e: Exception) {
                Log.e("test es", "Error in loadUser: ${e.message}")
                return@withContext false
            }
        }
    }


        /**
         * 좋아요 상태 업데이트
         * @param toiletId 화장실 ID
         * @param isLiked 좋아요 여부
         */
    fun updateLikeStatus(toiletId: Int, isLiked: Boolean) {
        val currentUser = _user.value ?: ToiletData.currentUser
        if (currentUser == null) {
            Log.e("test es", "Cannot update like status: No user logged in")
            return
        }

        viewModelScope.launch {
            try {
                // 현재 좋아요 목록 업데이트
                val updatedLikedToilets = (_likedToilets.value ?: setOf()).toMutableSet()
                if (isLiked) {
                    updatedLikedToilets.add(toiletId)
                } else {
                    updatedLikedToilets.remove(toiletId)
                }

                // LiveData 업데이트
                _likedToilets.value = updatedLikedToilets

                // Firestore 업데이트
                updateUserInFirestore(currentUser.email, updatedLikedToilets)
            } catch (e: Exception) {
                Log.e("test es", "Error updating like status: ${e.message}")
            }
        }
    }

    private fun updateUserInFirestore(email: String, likedToilets: Set<Int>) {
        viewModelScope.launch {
            try {
                val userRef = firestore.collection("users").document(email)

                // 트랜잭션을 사용하여 안전하게 업데이트
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(userRef)
                    if (snapshot.exists()) {
                        // 현재 사용자 데이터 가져오기
                        val currentData = snapshot.toObject(User::class.java)

                        // 업데이트할 데이터 준비
                        val updates = hashMapOf<String, Any>(
                            "likedToilets" to likedToilets.toList()
                        )

                        // 트랜잭션 내에서 업데이트 실행
                        transaction.update(userRef, updates)
                    }
                }.addOnSuccessListener {
                    Log.d("test es", "Successfully updated user likes in Firestore")
                    // 로컬 User 객체도 업데이트
                    _user.value?.let { currentUser ->
                        _user.value = currentUser.copy(likedToilets = likedToilets.toList())
                    }
                }.addOnFailureListener { e ->
                    Log.e("test es", "Error updating user in Firestore", e)
                    // 실패 시 로컬 상태 롤백을 위한 이벤트 발생 가능
                }
            } catch (e: Exception) {
                Log.e("test es", "Error in updateUserInFirestore: ${e.message}")
            }
        }
    }

}