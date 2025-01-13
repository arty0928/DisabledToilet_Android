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
    // 사용자 데이터를 Firebase에서 가져오기
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
     * 사용자 데이터를 이메일을 통해 Firebase에서 로드
     * ToiletData에 저장
     */
//    // 사용자 데이터 로드
//    fun loadUser(email: String) {
//        viewModelScope.launch {
//            try {
//                val document = firestore.collection("users").document(email).get().await()
//                val loadedUser = document.toObject(User::class.java)
//                loadedUser?.let {
//                    _user.value = it
//                    ToiletData.currentUser = it
//                }
//            } catch (e: Exception) {
//                Log.e("UserViewModel", "Error loading user: ${e.message}")
//            }
//        }
//    }

//    suspend fun loadUser(email: String): Boolean {
//        return withContext(Dispatchers.IO) {
//            try {
//                // Firestore에서 사용자 데이터 로드
//                val result = suspendCoroutine<Boolean> { continuation ->
//                    firestore.collection("users")
//                        .document(email)
//                        .get()
//                        .addOnSuccessListener { document ->
//                            if (document != null && document.exists()) {
//                                val user = document.toObject(User::class.java)
//
//                                user?.let {
//                                    _user.value = it
//                                    _likedToilets.value =
//                                        (it.likedToilets?.toSet() ?: setOf()) as MutableSet<Int>?
//
//                                    // email로 받아온 사용자 데이터 저장
//                                    ToiletData.currentUser = it
//
//                                    Log.d("test es", "User loaded: ${it.email}")
//                                    Log.d("test es", "Liked toilets loaded: ${_likedToilets.value}")
//                                    continuation.resume(true)
//                                } ?: continuation.resume(false)
//                            } else {
//                                Log.e("test es", "Document does not exist")
//                                continuation.resume(false)
//                            }
//                        }
//                        .addOnFailureListener { e ->
//                            Log.e("test es", "Error loading user: ${e.message}")
//                            continuation.resume(false)
//                        }
//                }
//                return@withContext result
//            } catch (e: Exception) {
//                Log.e("test es", "Error in loadUser: ${e.message}")
//                return@withContext false
//            }
//        }
//    }


        /**
         * 좋아요 상태 업데이트
         * @param toiletId 화장실 ID
         * @param isLiked 좋아요 여부
         */
//    fun updateLikeStatus(toiletId: Int, isLiked: Boolean) {
//        val currentUser = _user.value
//        if (currentUser == null) {
//            Log.e("test es", "Cannot update like status: No user logged in")
//            return
//        }
//
//        viewModelScope.launch {
//            val likedToilets = currentUser.likedToilets.value ?: mutableListOf()
//            if (isLiked) {
//                likedToilets.add(toiletId)
//            } else {
//                likedToilets.remove(toiletId)
//            }
//            currentUser.likedToilets.value = likedToilets
//
//            // ToiletData 업데이트
//            ToiletData.updateSaveValueForToilet(toiletId, currentUser.email, isLiked)
//
//            // Firebase 업데이트
//            syncUserToFirebase(currentUser)
//        }
//    }

//    // Firebase에 사용자 데이터 동기화
//    private fun syncUserToFirebase(user: User) {
//        viewModelScope.launch {
//            try {
//                firestore.collection("users").document(user.email).set(user).await()
//                Log.d("UserViewModel", "User data synced to Firebase")
//            } catch (e: Exception) {
//                Log.e("UserViewModel", "Error syncing user to Firebase: ${e.message}")
//            }
//        }
//    }

}