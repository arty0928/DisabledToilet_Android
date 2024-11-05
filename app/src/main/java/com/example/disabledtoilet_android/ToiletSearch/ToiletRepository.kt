package com.example.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.disabledtoilet_android.ToiletSearch.SearchFilter.FilterViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit



@RequiresApi(Build.VERSION_CODES.O)
class ToiletRepository {
    val Tag = "[ToiletRepository]"
    private var filterViewModel: FilterViewModel? = null
    private var filteredToiletList = listOf<ToiletModel>()
    private var isFilteredListInit = false

    fun getToiletWithSearchKeyword(
        toiletList: List<ToiletModel>,
        keyword: String
    ): MutableList<ToiletModel> {
        Log.d("test log", "filteredToiletList 사이즈: " + filteredToiletList.size.toString())

        val finalResult: MutableList<ToiletModel>

        if (isFilteredListInit){
            finalResult = applyQuery(filteredToiletList, keyword)
            Log.d("test log", "검색된 화장실 데이터 수: " + finalResult.size.toString())
        } else {
            finalResult = applyQuery(toiletList, keyword)
        }


        return finalResult
    }

    private fun applyQuery(toiletList: List<ToiletModel>, keyword: String): MutableList<ToiletModel>{
        val roadAddressResult = getToiletByRoadAddress(toiletList, keyword)
        val lotAddressResult = getToiletByLotAddress(toiletList, keyword)
        val nameResult = getToiletByToiletName(toiletList, keyword)

        val finalResult = getMergedResults(
            roadAddressResult,
            lotAddressResult,
            nameResult
        )

        Log.d("test log", "검색된 화장실 데이터 수: " + finalResult.size.toString())

        return finalResult
    }

    /**
     * 필터 세팅되면 바로 실행됨
     *
     */
    private fun setFilteredToiletList(
        filterViewModel: FilterViewModel,
        toiletList: MutableList<ToiletModel>
    ): List<ToiletModel> {
        Log.d("test log", "filteredToiletList built")

        var resultToiletList = mutableListOf<ToiletModel>()

        // 최근 점검
        when (filterViewModel.toiletRecentCheck.value) {
            filterViewModel.filterString.toiletCheckNever -> {
                resultToiletList = toiletList
            }

            //1년 이내
            filterViewModel.filterString.toiletCheckInYear -> {
                for (i in 0 until toiletList.size) {
                    if (isWithinOneYear(toiletList[i].data_reference_date)) {
                        resultToiletList.add(toiletList[i])
                    }
                }
                Log.d("test log", "남은 화장실 데이터 수: " + resultToiletList.size.toString())
            }

            //6개월 이내
            filterViewModel.filterString.toiletCheckHalfYear -> {
                for (i in 0 until toiletList.size) {
                    if (isWithinSixMonths(toiletList[i].data_reference_date)) {
                        resultToiletList.add(toiletList[i])
                    }
                }
                Log.d("test log", "남은 화장실 데이터 수: " + resultToiletList.size.toString())
            }

            //1달 이내
            filterViewModel.filterString.toiletCheckInMonth -> {
                for (i in 0 until toiletList.size) {
                    if (isWithinOneMonth(toiletList[i].data_reference_date)) {
                        resultToiletList.add(toiletList[i])
                    }
                }
                Log.d("test log", "남은 화장실 데이터 수: " + resultToiletList.size.toString())
            }
        }

        // 현재 운영
        if (filterViewModel.isToiletOperating.value!!){
            for (i in resultToiletList.size - 1 downTo 0){

            }
        }
        // 조건 적용

        isFilteredListInit = true

        return resultToiletList.toList()
    }

    fun getCurrentTime() {
        val currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedTime = currentTime.format(formatter)
        println("현재 시간: $formattedTime")
    }

    /**
     * 필터 세팅하고 바로 필터 적용한 화장실 리스트 만든다.
     */
    fun setFilter(filterViewModel: FilterViewModel, toiletList: List<ToiletModel>) {
        this.filterViewModel = filterViewModel

        //받은 toiletList 바로 넘김
        filteredToiletList = setFilteredToiletList(filterViewModel, toiletList.toMutableList())
    }


    fun getToiletByRoadAddress(
        toiletList: List<ToiletModel>,
        roadAddress: String
    ): MutableList<ToiletModel> {
        val tag = Tag + "[getToiletByRoadAddress]"
        Log.d(tag, "getToiletByRoadAddress called")
        var resultToiletList = mutableListOf<ToiletModel>()

        for (i in toiletList.indices) {
            val toilet = toiletList.get(i)
            val toiletRoadAddress = toilet.address_road

            if (toiletRoadAddress.contains(roadAddress)) {
                resultToiletList.add(toilet)
            }
        }

        return resultToiletList
    }


    private fun getToiletByLotAddress(
        toiletList: List<ToiletModel>,
        lotAddress: String
    ): MutableList<ToiletModel> {
        val tag = Tag + "[getToiletByLotAddress]"
        Log.d(tag, "called")
        val resultToiletList = mutableListOf<ToiletModel>()

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
        val resultToiletList = mutableListOf<ToiletModel>()

        for (i in 0 until toiletList.size) {
            val toilet = toiletList.get(i)
            val restroomName = toilet.restroom_name

            if (restroomName.contains(toiletName)) {
                resultToiletList.add(toilet)
                Log.d(tag, toilet.restroom_name)
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

    // 1년 이내 체크
    private fun isWithinOneYear(dateStr: String): Boolean {
        return isWithinPeriod(dateStr, 1, PeriodType.YEAR)
    }

    // 6개월 이내 체크
    private fun isWithinSixMonths(dateStr: String): Boolean {
        return isWithinPeriod(dateStr, 6, PeriodType.MONTH)
    }

    // 1개월 이내 체크
    private fun isWithinOneMonth(dateStr: String): Boolean {
        return isWithinPeriod(dateStr, 1, PeriodType.MONTH)
    }

    // 기간 체크를 위한 공통 함수
    private fun isWithinPeriod(dateStr: String, amount: Long, type: PeriodType): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        try {
            val date = LocalDate.parse(dateStr, formatter)
            val currentDate = LocalDate.now()

            val previousDate = when (type) {
                PeriodType.YEAR -> currentDate.minusYears(amount)
                PeriodType.MONTH -> currentDate.minusMonths(amount)
            }

            return !date.isBefore(previousDate) && !date.isAfter(currentDate)
        } catch (e: Exception) {
            return false
        }
    }

    private enum class PeriodType {
        YEAR, MONTH
    }

}






