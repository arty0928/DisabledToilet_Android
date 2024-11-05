package com.example.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import android.content.Context
import com.example.disabledtoilet_android.Utility.Dialog.LoadingDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ToiletData {
    private val TAG = "[ToiletData]"
    private val COLLECTION_NAME = "dreamhyoja" // Firebase Firestore의 컬렉션 이름
    private val PREFS_NAME = "ToiletCache"
    private val TOILETS_KEY = "ToiletList"

    private val firestore = FirebaseFirestore.getInstance()
    private val gson = Gson()
    private var listenerRegistration: ListenerRegistration? = null
    private lateinit var sharedPreferences: SharedPreferences

    val database: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://dreamhyoja-default-rtdb.asia-southeast1.firebasedatabase.app")
    var toiletListInit = false
    var cachedToiletList: List<ToiletModel>? = null

    fun initialize(onComplete: (Boolean) -> Unit) {
        // Firestore에서 데이터 로드
        val db = FirebaseFirestore.getInstance()
        db.collection("dreamhyoja") // "dreamhyoja" 컬렉션에서 데이터 가져오기
            .get()
            .addOnSuccessListener { documents ->
                // ToiletModel로 변환하여 cachedToiletList에 저장
                cachedToiletList = documents.mapNotNull { doc ->
                    ToiletModel.fromDocument(doc) // null이 아닌 경우만 포함
                }
                // 데이터 로드 성공 시 onComplete 호출
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                // 데이터 로드 실패 시 onComplete 호출
                Log.e(TAG, "Error loading data: ${exception.message}")
                onComplete(false)
            }
    }



    /**
     * 현재 지도 화면에 보이는 영역 내의 화장실을 필터링하여 반환합니다.
     *
     * @param southWest 남서쪽 경도/위도
     * @param northEast 북동쪽 경도/위도
     * @return 현재 화면 내에 위치한 화장실의 리스트
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

}
