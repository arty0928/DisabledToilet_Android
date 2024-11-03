package com.example.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.util.Log

class ToiletRepository {
    val Tag = "[ToiletRepository]"

    fun getToiletWithSearchKeyword(
        toiletList: List<ToiletModel>,
        keyword: String
    ): MutableList<ToiletModel> {
        var result = mutableListOf<ToiletModel>()

        val roadAddressResult = getToiletByRoadAddress(toiletList, keyword)
        val lotAddressResult = getToiletByLotAddress(result, keyword)
        val nameResult = getToiletByToiletName(result, keyword)

        val finalResult = getMergedResults(
            roadAddressResult,
            lotAddressResult,
            nameResult
        )
        return finalResult
    }

    fun getToiletByRoadAddress(
        toiletList: List<ToiletModel>,
        roadAddress: String
    ): MutableList<ToiletModel> {
        val tag = Tag + "[getToiletByRoadAddress]"
        Log.d(tag, "getToiletByRoadAddress called")
        var resultToiletList = mutableListOf<ToiletModel>()

        for (i in 0 until toiletList.size) {
            val toilet = toiletList.get(i)
            val toiletRoadAddress = toilet.address_road

            if (toiletRoadAddress.contains(roadAddress)) {
                resultToiletList.add(toilet)
                Log.d(tag, toilet.toString())
            }
        }

        return resultToiletList
    }


    fun getToiletByLotAddress(
        toiletList: List<ToiletModel>,
        lotAddress: String
    ): MutableList<ToiletModel> {
        val tag = Tag + "[getToiletByLotAddress]"
        Log.d(tag, "called")
        var resultToiletList = mutableListOf<ToiletModel>()

        for (i in 0 until toiletList.size) {
            val toilet = toiletList.get(i)
            val toiletLotAddress = toilet.address_lot

            if (toiletLotAddress.contains(lotAddress)) {
                resultToiletList.add(toilet)
            }
        }

        return resultToiletList
    }

    fun getToiletByToiletName(
        toiletList: List<ToiletModel>,
        toiletName: String
    ): MutableList<ToiletModel> {
        val tag = Tag + "[getToiletByToiletName]"
        Log.d(tag, "called")
        var resultToiletList = mutableListOf<ToiletModel>()

        for (i in 0 until toiletList.size) {
            val toilet = toiletList.get(i)
            val toiletName = toilet.restroom_name

            if (toiletName.contains(toiletName)) {
                resultToiletList.add(toilet)
            }
        }

        return resultToiletList
    }

    private fun getMergedResults(
        roadAddressResults: List<ToiletModel>,
        lotAddressResults: List<ToiletModel>,
        nameResults: List<ToiletModel>
    ): MutableList<ToiletModel> {
        val tag = Tag + "[getMergedResults]"
        Log.d(tag, "called")

        // HashSet을 사용하여 중복 제거
        val mergedResults = HashSet<ToiletModel>()

        // 각 결과를 Set에 추가
        mergedResults.addAll(roadAddressResults)
        mergedResults.addAll(lotAddressResults)
        mergedResults.addAll(nameResults)

        return mergedResults.toMutableList()
    }


}






