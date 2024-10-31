package com.example.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.util.Log

class ToiletRepository {
    val Tag = "[ToiletRepository]"
    var toiletList = ToiletData.toilets

    fun getToiletByRoadAddress(roadAddress: String): MutableList<ToiletModel>{
        val tag = Tag + "[getToiletByRoadAddress]"
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
        val tag = Tag + "[getToiletByLotAddress]"
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

}






