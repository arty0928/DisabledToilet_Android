package com.dream.disabledtoilet_android.ToiletSearch

import ToiletModel
import User
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dream.disabledtoilet_android.BuildConfig
import com.dream.disabledtoilet_android.ToiletSearch.ToiletData.currentUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ToiletData {
    private val TAG = "[ToiletData]"

    var toiletListInit = false
    // 전체 화장실 리스트 (불변)
    var cachedToiletList: List<ToiletModel>? = listOf()

    //좋아요가 변동된 화장실 리스트
    var updatedToilets = mutableListOf<ToiletModel>()

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
     * 화장실 정보 업데이트
     */
    fun updateToilet(toiletId : Int, isLiked : Boolean){
        val toilet = cachedToiletList?.find { it.number ==toiletId }
        toilet?.let{
            val originSaveValue = it.save
            it.save = if(isLiked) it.save + 1 else maxOf(0, it.save -1)

            if(originSaveValue != it.save && !updatedToilets.contains(it)){
                updatedToilets.add(it)
                Log.d("test es ", "Toilet updated : ${toiletId}, new save count : ${it.save}")
            }
        }
    }

    /**
     * 업데이트 된 화장실 정보만 firebase에 업데이트
     */
    fun syncToFirebase() {
        updatedToilets.forEach { toilet ->
            firestore.collection("dreamhyoja")
                .document(toilet.number.toString())
                .set(toilet)
                .addOnSuccessListener {
                    Log.d(TAG, "Toilet data synced successfully: ${toilet.number}")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error syncing toilet data: ${exception.message}")
                }
        }
        updatedToilets.clear()
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
