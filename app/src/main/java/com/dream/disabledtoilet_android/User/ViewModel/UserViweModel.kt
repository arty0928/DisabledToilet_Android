package com.dream.disabledtoilet_android.User.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dream.disabledtoilet_android.ToiletSearch.ToiletData
import com.dream.disabledtoilet_android.User.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.Callback

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
    /**
     * 사용자 데이터를 이메일을 통해 Firebase에서 로드
     * ToiletData에 저장
     */
    suspend fun loadUser(email: String): Boolean {
        Log.d("test loadUser", "Starting loadUser for email: $email")

        return withContext(Dispatchers.Default) {
            Log.d("test loadUser", "Entered withContext block")

            try {
                val document = firestore.collection("users")
                    .document(email)
                    .get()
                    .await()

                // document 데이터 로깅 추가
                Log.d("test loadUser", "Document exists: ${document.exists()}")
                Log.d("test loadUser", "Document data: ${document.data}")

                val user = document.toObject(User::class.java)

                if (user != null) {
                    Log.d("test loadUser", "User found: $user")
                    ToiletData.currentUser = user
                    _user.postValue(user)  // LiveData 업데이트 추가
                    _likedToilets.postValue(user.likedToilets?.toMutableSet() ?: mutableSetOf())
                    true
                } else {
                    Log.d("test loadUser", "User not found in Firestore")
                    false
                }
            } catch (e: Exception) {
                Log.e("test loadUser", "Error loading user: ${e.message}", e)
                false
            }
        }
    }



    /**
     * 좋아요 상태 업데이트
     * @param toiletId 화장실 ID
     * @param isLiked 좋아요 여부
     */
    fun updateLikeStatus(toiletId: Int, isLiked: Boolean) {
        val currentUser = _user.value
        if (currentUser != null) {
            val updatedLikedToilets = _likedToilets.value ?: mutableSetOf()

            if (isLiked) {
                updatedLikedToilets.add(toiletId)
            } else {
                updatedLikedToilets.remove(toiletId)
            }

            // LiveData 업데이트
            _likedToilets.value = updatedLikedToilets


            // Firebase에 사용자 데이터 업데이트
            currentUser.likedToilets = updatedLikedToilets.toMutableList()
            firestore.collection("users")
                .document(currentUser.email)
                .set(currentUser)
                .addOnSuccessListener {
                    _user.postValue(currentUser)
                }
                .addOnFailureListener {
                    // 에러 처리
                }
        }
    }


    /**
     * 특정 화장실이 사용자의 좋아요 목록에 있는지 확인
     * @param toiletId 화장실 ID
     * @return 좋아요 여부
     */
    fun isToiletLiked(toiletId: Int): Boolean {
        return _likedToilets.value?.contains(toiletId) ?: false
    }

    /**
     * Firebase에 저장된 사용자 데이터를 삭제 (로그아웃 시 호출)
     */
    fun clearUserData() {
        _user.value = null
        _likedToilets.value = mutableSetOf()
    }
}
