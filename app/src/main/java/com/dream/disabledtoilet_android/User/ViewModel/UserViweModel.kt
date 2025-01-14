package com.dream.disabledtoilet_android.User.ViewModel

import ToiletModel
import User
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel(private val email :String) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // 사용자 전체 데이터를 저장하는 LiveData
    private val _userData = MutableLiveData<User?>()
    val userData: MutableLiveData<User?> get() = _userData

    init {
        // viewModel 초기화 시 사용자 데이터 가져오기
        loadUser()
    }
    /**
     * 초기화
     */
    // 사용자 데이터를 Firebase에서 가져오기 -> 레포지토리로
    fun loadUser() {
        firestore.collection("users")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.data
                    if (data != null) {
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
                        _userData.postValue(user)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UserViewModel", "Failed to load user data: ${exception.message}")
            }
    }


    // 좋아요 상태를 토글하고 결과 반환
    fun toggleLikeStatus(toiletId: Int): Boolean {
        val currentUser = _userData.value ?: return false

        val likedToilets = currentUser.likedToilets.toMutableSet() ?: mutableSetOf()

        val isLiked: Boolean
        if (likedToilets.contains(toiletId)) {
            likedToilets.remove(toiletId)
            isLiked = false
        } else {
            likedToilets.add(toiletId)
            isLiked = true
        }

        // 업데이트된 좋아요 리스트로 사용자 데이터 갱신
        currentUser.likedToilets = likedToilets.toList()
        _userData.value = currentUser

        // Firebase에 동기화
        firestore.collection("users")
            .document(email)
            .set(currentUser)
            .addOnSuccessListener {
                Log.d("UserViewModel", "Like status synced with Firebase.")
            }
            .addOnFailureListener { exception ->
                Log.e("UserViewModel", "Failed to sync like status: ${exception.message}")
            }

        return isLiked
    }

    /**
     * UserViewModel에 데이터를 Firebase에 동기화하는 메서드
     */
    fun syncUserDataToFirebase(user: User) {
        firestore.collection("users")
            .document(email)
            .set(user)
            .addOnSuccessListener {
                Log.d("UserViewModel", "User data synced with Firebase.")
            }
            .addOnFailureListener { exception ->
                Log.e("UserViewModel", "Failed to sync user data: ${exception.message}")
            }
    }

    //-> 레포지토리로
}