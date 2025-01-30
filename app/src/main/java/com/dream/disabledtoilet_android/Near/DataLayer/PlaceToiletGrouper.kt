package com.dream.disabledtoilet_android.Near.DataLayer

import android.content.Context
import android.util.Log
import com.dream.disabledtoilet_android.Model.PlaceModel
import com.dream.disabledtoilet_android.Model.ToiletModel
import com.dream.disabledtoilet_android.Utility.Database.PlaceToiletGroupDatabase.PlaceToiletGroupRepo
import com.dream.disabledtoilet_android.Utility.Database.ToiletDatabase.HyojaDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PlaceToiletGrouper(context: Context) {
    val db = HyojaDatabase.getDatabase(context)
    val placeDao = db.placeDao()
    val toiletDao = db.toiletDao()
    val placeToiletGroupDao = db.placeToiletGroupDao()
    val repo = PlaceToiletGroupRepo(placeToiletGroupDao, placeDao, toiletDao)

    /**
     * 장소 저장 후, 화장실하고 연결
     */
    fun savePlaceAndGroup(place: PlaceModel, toilet: ToiletModel){
        GlobalScope.launch {
            repo.insertPlace(place)
            repo.linkPlaceAndToilet(place.id, toilet.documentId)
        }
    }

    fun unSavePlaceAndGroup(place: PlaceModel, toilet: ToiletModel){
        GlobalScope.launch {
            val data = repo.getPlaceToiletGroupByPlaceAndToiletId(place.id, toilet.documentId)
            if (!data.isNullOrEmpty()) {
                repo.deletePlaceToiletGroup(data[0])
            }
        }
    }

    suspend fun isSaved(place: PlaceModel, toilet: ToiletModel): Boolean{
        var result = true
        Log.d("test log", "isSaved called")
        val group = repo.getPlaceToiletGroupByPlaceAndToiletId(place.id, toilet.documentId)
        Log.d("test log", "isSaved called2")
        if (group.isNullOrEmpty()){
            result = false
        }

        return result
    }
}