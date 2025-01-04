package com.dream.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dream.disabledtoilet_android.BuildConfig
import com.dream.disabledtoilet_android.User.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ToiletData {
    private val TAG = "[ToiletData]"

    //좋아요 데이터 변동 감지
    private val _save = MutableLiveData<Int>()
    var toiletListInit = false
    var cachedToiletList: List<ToiletModel>? = listOf()

    //사용자
    var currentUser : User? = null

    // Firestore 인스턴스
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun initialize(): Boolean = suspendCoroutine { continuation ->
        // Firestore에서 데이터 로드
        firestore.collection("dreamhyoja") // "dreamhyoja" 컬렉션에서 데이터 가져오기
            .get()
            .addOnSuccessListener { documents ->
                // ToiletModel로 변환하여 cachedToiletList에 저장
                cachedToiletList = documents.mapNotNull { doc ->
                    ToiletModel.fromDocument(doc) // null이 아닌 경우만 포함
                }
                // 데이터 로드 성공 시 true 반환
                toiletListInit = true

                continuation.resume(true)
            }
            .addOnFailureListener { exception ->
                // 데이터 로드 실패 시 false 반환
                Log.e(TAG, "Error loading data: ${exception.message}")
                continuation.resume(false)
            }
    }

    /**
     * 특정 화장실의 좋아요 값 업데이트 (로컬 데이터)
     */
    fun updateSaveValueForToilet(toiletId: Int, userId: String, isLiked: Boolean) {
        val toilet = cachedToiletList?.find { it.number == toiletId }
        toilet?.let {
            if (isLiked) {
                // 사용자의 likedToilets에 화장실 ID 추가
                if (!currentUser?.likedToilets?.contains(toiletId)!! == true) {
                    currentUser?.likedToilets?.add(toiletId)
                    it.save += 1 // save 값 증가
                }
            } else {
                // 사용자의 likedToilets에서 화장실 ID 제거
                if (currentUser?.likedToilets?.contains(toiletId) == true) {
                    currentUser?.likedToilets?.remove(toiletId)
                    it.save = maxOf(0, it.save - 1) // save 값 감소 (0 이하로 내려가지 않도록 처리)
                }
            }
        }
    }


    /**
     * 사용자 정보 업데이트 (즉시 Firebase 반영)
     */
    fun updateUserLikes(toiletId: Int, isLiked: Boolean) {
        currentUser?.let { user ->
            if (isLiked) {
                if (!user.likedToilets.contains(toiletId)) user.likedToilets.add(toiletId)
            } else {
                user.likedToilets.remove(toiletId)
            }

            // Firebase에 사용자 정보 업데이트
            firestore.collection("users")
                .document(user.email)
                .set(user)
                .addOnSuccessListener {
                    Log.d("test es", "User data updated successfully.")
                }
                .addOnFailureListener { exception ->
                    Log.e("test es", "Error updating user data: ${exception.message}")
                }
        }
    }

    /**
     * Firebase에 로컬 화장실 데이터 동기화
     */
    fun syncToFirebase() {
        cachedToiletList?.forEach { toilet ->
            firestore.collection("dreamhyoja")
                .document(toilet.number.toString())
                .set(toilet)
                .addOnSuccessListener {
                    Log.d("test es", "Toilet data synced successfully: ${toilet.number}")
                }
                .addOnFailureListener { exception ->
                    Log.e("test es", "Error syncing toilet data: ${exception.message}")
                }
        }
    }

    fun getToiletAllData() : List<ToiletModel>? {
        return ToiletData.cachedToiletList
    }


    fun getCurretnUser() : User?{
        return currentUser
    }

    /**
     * 로그아웃 -> 저장된 사용자 데이터 삭제
     */
    fun clearCurrentUser(){
        currentUser = null
    }
}
