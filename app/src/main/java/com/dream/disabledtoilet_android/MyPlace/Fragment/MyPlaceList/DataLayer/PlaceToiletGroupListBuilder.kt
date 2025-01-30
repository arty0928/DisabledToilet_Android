package com.dream.disabledtoilet_android.MyPlace.Fragment.MyPlaceList.DataLayer

import android.content.Context
import com.dream.disabledtoilet_android.Model.ToiletListModel
import com.dream.disabledtoilet_android.Utility.Database.PlaceToiletGroupDatabase.PlaceToiletGroupRepo
import com.dream.disabledtoilet_android.Utility.Database.ToiletDatabase.HyojaDatabase

class PlaceToiletGroupListBuilder(context: Context) {
    val db = HyojaDatabase.getDatabase(context)
    val placeToiletGroupDao = db.placeToiletGroupDao()
    val placeDao = db.placeDao()
    val toiletDao = db.toiletDao()
    val repo = PlaceToiletGroupRepo(placeToiletGroupDao, placeDao, toiletDao)

    suspend fun makeList(): List<ToiletListModel>{
        var resultList = mutableListOf<ToiletListModel>()
        val placeList = repo.getAllPlaces()
        for (place in placeList){
            val toiletList = repo.getToiletsByPlaceId(place.id)
            resultList.add(ToiletListModel(place, toiletList))
        }
        return resultList
    }
}