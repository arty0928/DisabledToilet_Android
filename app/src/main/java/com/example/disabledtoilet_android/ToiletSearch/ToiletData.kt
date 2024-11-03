package com.example.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import android.content.Context
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
    private val toiletList = mutableListOf<ToiletModel>()
    private var listenerRegistration: ListenerRegistration? = null
    private lateinit var sharedPreferences: SharedPreferences

    val database: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://dreamhyoja-default-rtdb.asia-southeast1.firebasedatabase.app")
    var toiletListInit = false
    var cachedToiletList: List<ToiletModel>? = null

    fun getToiletAllData(onSuccess: (List<ToiletModel>) -> Unit, onFailure: (Exception) -> Unit) {
        if (cachedToiletList != null) {
            // 캐시된 데이터가 있는 경우
            onSuccess(cachedToiletList!!)
        } else {
            // Firestore에서 데이터 로드
            val db = FirebaseFirestore.getInstance()
            db.collection("dreamhyoja")
                .get()
                .addOnSuccessListener { documents ->
                    cachedToiletList = cleanToiletList(
                        documents.map { doc ->
                            ToiletModel.fromDocument(doc)
                        }
                    )

                    // 데이터 정리해서 넣기
                    onSuccess(cachedToiletList!!)
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
            toiletListInit = true
        }
    }

    // 데이터 정리 함수
    fun cleanToiletList(toiletList: List<ToiletModel>): List<ToiletModel> {
        val resultList = mutableListOf<ToiletModel>()

        // 이름 비어있는 아이템 제외
        for (i in 0 until toiletList.size) {
            var toiletName = toiletList[i].restroom_name

            if (toiletName.isNotBlank() && toiletName != "\"\"") {
                resultList.add(toiletList[i])
            }
        }

        return resultList.toList()
    }

    fun initialize(context: Context, onComplete: (Boolean) -> Unit) {
        ToiletData.sharedPreferences = context.getSharedPreferences(ToiletData.PREFS_NAME, Context.MODE_PRIVATE)
        loadCachedData()

        // 캐시가 비어 있으면 데이터를 가져옵니다.
        if (toiletList.isEmpty()) {
            Log.d(TAG, "Cache is empty, fetching data from Firestore.")
            fetchAllToilets { success ->
                if (success) {
                    listenToToiletUpdates()
                }
                onComplete(success)
            }
        } else {
            // 이미 캐시된 데이터가 있는 경우, 실시간 업데이트 리스너만 설정
            listenToToiletUpdates()
            onComplete(true)
        }
    }

    /**
     * 캐시된 데이터를 로드
     */
    private fun loadCachedData() {
        val json = ToiletData.sharedPreferences.getString(ToiletData.TOILETS_KEY, null)
        if (json != null) {
            val type = object : TypeToken<MutableList<ToiletModel>>() {}.type
            val cachedList: MutableList<ToiletModel> = ToiletData.gson.fromJson(json, type)
            ToiletData.toiletList.clear()
            ToiletData.toiletList.addAll(cachedList)
            Log.d(ToiletData.TAG, "Loaded ${ToiletData.toiletList.size} toilets from cache.")
        } else {
            Log.d(ToiletData.TAG, "No cached toilet data found.")

        }
    }

    /**
     * Firebase에서 모든 화장실 데이터를 가져와 리스트에 저장 및 캐싱
     */
    private fun fetchAllToilets(onComplete: (Boolean) -> Unit) {
        ToiletData.firestore.collection(ToiletData.COLLECTION_NAME)
            .get()
            .addOnSuccessListener { result ->
                ToiletData.toiletList.clear()
                for (document in result) {
                    val toilet = document.toObject(ToiletModel::class.java)
                    ToiletData.toiletList.add(toilet)
                }
                cacheData()
                Log.d(TAG, "Fetched ${ToiletData.toiletList.size} toilets from Firebase.")
                onComplete(true)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching toilets: ${exception.message}", exception)
                onComplete(false)
            }
    }

    /**
     * Firestore의 실시간 업데이트 리스너 설정
     */
    private fun listenToToiletUpdates() {
        ToiletData.listenerRegistration = ToiletData.firestore.collection(
            ToiletData.COLLECTION_NAME
        )
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e(ToiletData.TAG, "Listen failed: ${error.message}", error)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        com.google.firebase.firestore.DocumentChange.Type.ADDED -> {
                            val newToilet = dc.document.toObject(ToiletModel::class.java)
                            ToiletData.toiletList.add(newToilet)
                            Log.d(ToiletData.TAG, "New toilet added: ${newToilet.restroom_name}")
                        }
                        com.google.firebase.firestore.DocumentChange.Type.MODIFIED -> {
                            val modifiedToilet = dc.document.toObject(ToiletModel::class.java)
                            val index = ToiletData.toiletList.indexOfFirst { it.restroom_name == modifiedToilet.restroom_name } // 고유 식별자 필요
                            if (index != -1) {
                                ToiletData.toiletList[index] = modifiedToilet
                                Log.d(ToiletData.TAG, "Toilet modified: ${modifiedToilet.restroom_name}")
                            }
                        }
                        com.google.firebase.firestore.DocumentChange.Type.REMOVED -> {
                            val removedToilet = dc.document.toObject(ToiletModel::class.java)
                            ToiletData.toiletList.removeAll { it.restroom_name == removedToilet.restroom_name } // 고유 식별자 필요
                            Log.d(ToiletData.TAG, "Toilet removed: ${removedToilet.restroom_name}")
                        }
                    }
                }
                cacheData() // 업데이트된 데이터를 캐싱
            }
    }


    /**
     * 캐시 데이터를 SharedPreferences에 저장
     */
    private fun cacheData() {
        val json = ToiletData.gson.toJson(ToiletData.toiletList)
        ToiletData.sharedPreferences.edit().putString(ToiletData.TOILETS_KEY, json).apply()
        Log.d(ToiletData.TAG, "Cached ${ToiletData.toiletList.size} toilets.")
    }


    /**
     * 현재 지도 화면에 보이는 영역 내의 화장실을 필터링하여 반환합니다.
     *
     * @param southWest 남서쪽 경도/위도
     * @param northEast 북동쪽 경도/위도
     * @return 현재 화면 내에 위치한 화장실의 리스트
     */
    fun getToiletsWithinBounds(southWestLatitude: Double, southWestLongitude: Double, northEastLatitude: Double, northEastLongitude: Double): List<ToiletModel> {
        return ToiletData.toiletList.filter { toilet ->
            toilet.wgs84_latitude in southWestLatitude..northEastLatitude &&
                    toilet.wgs84_longitude in southWestLongitude..northEastLongitude
        }
    }

    fun getAllToilets() : List<ToiletModel>{
        return ToiletData.toiletList
    }


    /**
     * 리포지토리 정리 시 호출
     */
    fun clearListener() {
        ToiletData.listenerRegistration?.remove()
    }
}
