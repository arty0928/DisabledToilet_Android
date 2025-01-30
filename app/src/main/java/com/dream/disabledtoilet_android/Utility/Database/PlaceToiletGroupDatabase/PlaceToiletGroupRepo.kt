package com.dream.disabledtoilet_android.Utility.Database.PlaceToiletGroupDatabase

import com.android.tools.build.jetifier.core.utils.Log
import com.dream.disabledtoilet_android.Model.PlaceModel
import com.dream.disabledtoilet_android.Model.PlaceToiletGroupModel
import com.dream.disabledtoilet_android.Model.ToiletModel
import com.dream.disabledtoilet_android.Utility.Database.PlaceDatabase.PlaceDAO
import com.dream.disabledtoilet_android.Utility.Database.ToiletDatabase.ToiletDAO

class PlaceToiletGroupRepo(
    private val placeToiletGroupDao: PlaceToiletGroupDao,
    private val placeDao: PlaceDAO,
    private val toiletDao: ToiletDAO
) {

    // PlaceToiletGroupModel 관련 작업
    suspend fun insertPlaceToiletGroup(placeToiletGroup: PlaceToiletGroupModel) {
        placeToiletGroupDao.insert(placeToiletGroup)
    }

    suspend fun updatePlaceToiletGroup(placeToiletGroup: PlaceToiletGroupModel) {
        placeToiletGroupDao.update(placeToiletGroup)
    }

    suspend fun deletePlaceToiletGroup(placeToiletGroup: PlaceToiletGroupModel) {
        placeToiletGroupDao.delete(placeToiletGroup)
    }

    suspend fun getAllPlaceToiletGroups(): List<PlaceToiletGroupModel> {
        return placeToiletGroupDao.getAllGroups()
    }

    suspend fun getPlaceToiletGroupByPlaceId(placeId: String): List<PlaceToiletGroupModel>? {
        return placeToiletGroupDao.getGroupByPlaceId(placeId)
    }

    suspend fun getPlaceToiletGroupByToiletId(toiletId: String): List<PlaceToiletGroupModel>? {
        return placeToiletGroupDao.getGroupByToiletId(toiletId)
    }

    suspend fun getPlaceToiletGroupByPlaceAndToiletId(placeId: String, toiletId: String): List<PlaceToiletGroupModel>? {
        Log.d("test log", "getPlaceToiletGroupByPlaceAndToiletId called")
        return  placeToiletGroupDao.getGroupByPlaceAndToiletId(placeId, toiletId)
    }

    // PlaceModel 관련 작업
    suspend fun insertPlace(place: PlaceModel) {
        placeDao.insertPlace(place)
    }

    suspend fun getPlaceById(placeId: String): PlaceModel? {
        return placeDao.getAllPlaces().find { it.id == placeId }
    }

    suspend fun getAllPlaces(): List<PlaceModel> {
        return placeDao.getAllPlaces()
    }

    // ToiletModel 관련 작업
    suspend fun insertToilet(toilet: ToiletModel) {
        toiletDao.insertToilet(toilet)
    }

    suspend fun getToiletById(toiletId: String): ToiletModel? {
        return toiletDao.getAllToilets().find { it.documentId == toiletId }
    }

    suspend fun getAllToilets(): List<ToiletModel> {
        return toiletDao.getAllToilets()
    }

    // 연관 데이터 작업: 특정 Place에 연결된 Toilet 조회
    suspend fun getToiletsByPlaceId(placeId: String): List<ToiletModel> {
        val groups = placeToiletGroupDao.getAllGroups().filter { it.placeId == placeId }
        val toiletIds = groups.map { it.toiletId }
        return toiletDao.getAllToilets().filter { toiletIds.contains(it.documentId) }
    }

    // 연관 데이터 작업: 특정 Toilet에 연결된 Place 조회
    suspend fun getPlacesByToiletId(toiletId: String): List<PlaceModel> {
        val groups = placeToiletGroupDao.getAllGroups().filter { it.toiletId == toiletId }
        val placeIds = groups.map { it.placeId }
        return placeDao.getAllPlaces().filter { placeIds.contains(it.id) }
    }

    // Place와 Toilet을 연결하는 작업
    suspend fun linkPlaceAndToilet(placeId: String, toiletId: String) {
        val group = PlaceToiletGroupModel(
            groupId = "${placeId}_$toiletId", // groupId를 placeId_toiletId로 생성
            placeId = placeId,
            toiletId = toiletId
        )
        placeToiletGroupDao.insert(group)
    }

    // 특정 Place와 연결된 모든 Toilets 삭제
    suspend fun deleteToiletsByPlaceId(placeId: String) {
        val groups = placeToiletGroupDao.getAllGroups().filter { it.placeId == placeId }
        for (group in groups) {
            placeToiletGroupDao.delete(group)
        }
    }

    // 특정 Toilet과 연결된 모든 Places 삭제
    suspend fun deletePlacesByToiletId(toiletId: String) {
        val groups = placeToiletGroupDao.getAllGroups().filter { it.toiletId == toiletId }
        for (group in groups) {
            placeToiletGroupDao.delete(group)
        }
    }
}