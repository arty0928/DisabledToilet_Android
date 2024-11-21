package com.example.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.disabledtoilet_android.BuildConfig
import com.example.disabledtoilet_android.User.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ToiletData {
    private val TAG = "[ToiletData]"
    private val COLLECTION_NAME = "dreamhyoja" // Firebase Firestore의 컬렉션 이름
    private val PREFS_NAME = "ToiletCache"
    private val TOILETS_KEY = "ToiletList"

    //좋아요 데이터 변동 감지
    private val _save = MutableLiveData<Int>()
    val save : MutableLiveData<Int> get() = _save


    val database: FirebaseDatabase =
        FirebaseDatabase.getInstance(BuildConfig.FIREBASE)
    var toiletListInit = false
    var cachedToiletList: List<ToiletModel>? = listOf()

    //사용자
    var currentUser : User? = null

    suspend fun initialize(): Boolean = suspendCoroutine { continuation ->
        // Firestore에서 데이터 로드
        val db = FirebaseFirestore.getInstance()
        db.collection("dreamhyoja") // "dreamhyoja" 컬렉션에서 데이터 가져오기
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

    // save 값 업데이트 (전체 값에 대한 업데이트)
    fun updateSaveValueForToilet(toiletNumber: Int, newSaveValue: Int) {
        // 해당 화장실 찾기
        val toilet = cachedToiletList?.find { it.number == toiletNumber }
        toilet?.save = newSaveValue // 해당 화장실의 save 값을 업데이트
    }


    /**
     *          현재 지도 화면에 보이는 영역 내의 화장실을 필터링하여 반환합니다.
     *          @param southWest 남서쪽 경도/위도
     *          @param northEast 북동쪽 경도/위도
     *          @return 현재 화면 내에 위치한 화장실의 리스트
     */
    fun getToiletsWithinBounds(southWestLatitude: Double, southWestLongitude: Double, northEastLatitude: Double, northEastLongitude: Double): List<ToiletModel> {
        return ToiletData.cachedToiletList!!.filter { toilet ->
            toilet.wgs84_latitude in southWestLatitude..northEastLatitude &&
                    toilet.wgs84_longitude in southWestLongitude..northEastLongitude
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
