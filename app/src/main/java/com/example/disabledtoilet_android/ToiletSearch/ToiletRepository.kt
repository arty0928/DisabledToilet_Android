package com.example.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ToiletRepository {
    private val TAG = "[ToiletRepository]"
    private val COLLECTION_NAME = "dreamhyoja" // Firebase Firestore의 컬렉션 이름
    private val PREFS_NAME = "ToiletCache"
    private val TOILETS_KEY = "ToiletList"

    private val firestore = FirebaseFirestore.getInstance()
    private val gson = Gson()
    private val toiletList = mutableListOf<ToiletModel>()
    private var listenerRegistration: ListenerRegistration? = null
    private lateinit var sharedPreferences: SharedPreferences


    /**
     * 초기화 함수, 앱 시작 시 호출
     */
    fun initialize(context: Context, onComplete: (Boolean) -> Unit) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadCachedData()

        fetchAllToilets { success ->
            if (success) {
                listenToToiletUpdates()
            }
            onComplete(success)
        }
    }

    /**
     * 캐시된 데이터를 로드
     */
    private fun loadCachedData() {
        val json = sharedPreferences.getString(TOILETS_KEY, null)
        if (json != null) {
            val type = object : TypeToken<MutableList<ToiletModel>>() {}.type
            val cachedList: MutableList<ToiletModel> = gson.fromJson(json, type)
            toiletList.clear()
            toiletList.addAll(cachedList)
            Log.d(TAG, "Loaded ${toiletList.size} toilets from cache.")
        } else {
            Log.d(TAG, "No cached toilet data found.")
        }
    }

    /**
     * Firebase에서 모든 화장실 데이터를 가져와 리스트에 저장 및 캐싱
     */
    private fun fetchAllToilets(onComplete: (Boolean) -> Unit) {
        firestore.collection(COLLECTION_NAME)
            .get()
            .addOnSuccessListener { result ->
                toiletList.clear()
                for (document in result) {
                    val toilet = document.toObject(ToiletModel::class.java)
                    toiletList.add(toilet)
                }
                cacheData()
                Log.d(TAG, "Fetched ${toiletList.size} toilets from Firebase.")
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
        listenerRegistration = firestore.collection(COLLECTION_NAME)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e(TAG, "Listen failed: ${error.message}", error)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        com.google.firebase.firestore.DocumentChange.Type.ADDED -> {
                            val newToilet = dc.document.toObject(ToiletModel::class.java)
                            toiletList.add(newToilet)
                            Log.d(TAG, "New toilet added: ${newToilet.restroom_name}")
                        }
                        com.google.firebase.firestore.DocumentChange.Type.MODIFIED -> {
                            val modifiedToilet = dc.document.toObject(ToiletModel::class.java)
                            val index = toiletList.indexOfFirst { it.restroom_name == modifiedToilet.restroom_name } // 고유 식별자 필요
                            if (index != -1) {
                                toiletList[index] = modifiedToilet
                                Log.d(TAG, "Toilet modified: ${modifiedToilet.restroom_name}")
                            }
                        }
                        com.google.firebase.firestore.DocumentChange.Type.REMOVED -> {
                            val removedToilet = dc.document.toObject(ToiletModel::class.java)
                            toiletList.removeAll { it.restroom_name == removedToilet.restroom_name } // 고유 식별자 필요
                            Log.d(TAG, "Toilet removed: ${removedToilet.restroom_name}")
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
        val json = gson.toJson(toiletList)
        sharedPreferences.edit().putString(TOILETS_KEY, json).apply()
        Log.d(TAG, "Cached ${toiletList.size} toilets.")
    }


    /**
     * 현재 지도 화면에 보이는 영역 내의 화장실을 필터링하여 반환합니다.
     *
     * @param southWest 남서쪽 경도/위도
     * @param northEast 북동쪽 경도/위도
     * @return 현재 화면 내에 위치한 화장실의 리스트
     */
    fun getToiletsWithinBounds(southWestLatitude: Double, southWestLongitude: Double, northEastLatitude: Double, northEastLongitude: Double): List<ToiletModel> {
        return toiletList.filter { toilet ->
            toilet.wgs84_latitude in southWestLatitude..northEastLatitude &&
                    toilet.wgs84_longitude in southWestLongitude..northEastLongitude
        }
    }

    fun getAllToilets() : List<ToiletModel>{
        return toiletList
    }


    fun getToiletByRoadAddress(roadAddress: String): MutableList<ToiletModel>{
        val tag = TAG + "[getToiletByRoadAddress]"
        Log.d(tag,"getToiletByRoadAddress called")
        var resultToiletList = mutableListOf<ToiletModel>()

        for (i in 0 until toiletList.size){
            val toilet = toiletList.get(i)
            val toiletRoadAddress = toilet.address_road

            if (toiletRoadAddress.contains(roadAddress)){
                resultToiletList.add(toilet)
                Log.d(tag,toilet.toString())
            }
        }

        return resultToiletList
    }

    fun getToiletByLotAddress(lotAddress: String): MutableList<ToiletModel>{
        val tag = TAG + "[getToiletByLotAddress]"
        Log.d(tag,"called")
        var resultToiletList = mutableListOf<ToiletModel>()

        for (i in 0 until toiletList.size){
            val toilet = toiletList.get(i)
            val toiletLotAddress = toilet.address_lot

            if (toiletLotAddress.contains(lotAddress)){
                resultToiletList.add(toilet)
                Log.d(tag,toilet.toString())
            }
        }

        return resultToiletList
    }

    /**
     * 리포지토리 정리 시 호출
     */
    fun clearListener() {
        listenerRegistration?.remove()
    }

}






