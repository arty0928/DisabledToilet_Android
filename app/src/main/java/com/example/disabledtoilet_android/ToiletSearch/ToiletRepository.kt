package com.example.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.disabledtoilet_android.ToiletSearch.SearchFilter.FilterViewModel
import org.apache.commons.lang3.mutable.Mutable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * 화장실 데이터 처리 클래스
 * 조건 검색 다이얼로그에서 필터링하면 해당 클래스에 반영됨(filteredToiletList)
 */
@RequiresApi(Build.VERSION_CODES.O)
class ToiletRepository {
    val Tag = "[ToiletRepository]"
    private var filterViewModel: FilterViewModel? = null
    private var filteredToiletList = listOf<ToiletModel>()
    private var isFilteredListInit = false
    /**
     * 키워드 검색 함수
     */
    fun getToiletWithSearchKeyword(
        toiletList: List<ToiletModel>,
        keyword: String
    ): MutableList<ToiletModel> {
        Log.d("test log", "filteredToiletList 사이즈: " + filteredToiletList.size.toString())
        val finalResult: MutableList<ToiletModel>
        // 조건 검색 필터링 반영된 리스트 있는지 체크
        if (isFilteredListInit) {
            // 필터링 반영된 리스트로 applyQuery
            finalResult = applyQuery(filteredToiletList, keyword)
            Log.d("test log", "검색된 화장실 데이터 수: " + finalResult.size.toString())
        } else {
            // 넘겨준 리스트로 applyQuery
            finalResult = applyQuery(toiletList, keyword)
        }
        return finalResult
    }
    /**
     * 키워드로 검색한 내용들 합침
     */
    private fun applyQuery(
        toiletList: List<ToiletModel>,
        keyword: String
    ): MutableList<ToiletModel> {
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
     * 필터 세팅할때 호출하는 함수
     * 필터 세팅하고 바로 필터 적용한 화장실 리스트 만든다.
     * 기본적으로 넘겨받은 toiletList로 filteredList 생성
     */
    fun setFilter(filterViewModel: FilterViewModel, toiletList: List<ToiletModel>) {
        //뷰모델 받아서 세팅
        this.filterViewModel = filterViewModel
        //받은 toiletList 바로 넘김
        filteredToiletList = setFilteredToiletList(filterViewModel, toiletList.toMutableList())
    }
    /**
     * 조건적용 다이얼로그 데이터를 반영한 필터 리스트 생성.
     * 필터 세팅되면 바로 실행됨
     * 넘겨받은 toiletList이용해서 fliteredList 생성
     */
    private fun setFilteredToiletList(
        filterViewModel: FilterViewModel,
        toiletList: MutableList<ToiletModel>
    ): List<ToiletModel> {
        Log.d("test log", "filteredToiletList built")
        //여기에 추가해가는 방식
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
        if (filterViewModel.isToiletOperating.value!!) {
            for (i in resultToiletList.size - 1 downTo 0) {
                // 이거 어떻게 하지
            }
        }
        //조건 적용
        val filterList = filterViewModel.filterLiveList.value
        val nameList = filterViewModel.filterString.filterNameList
        for (i in 0 until filterList!!.size){
            when(filterList[i].filterName){
                nameList[0] -> {
                    if (filterList[i].checked){
                        // resultToiletList에서 데이터 추출
                        resultToiletList = extractDisableUrinalExistingToilet(resultToiletList)
                    }
                }
                nameList[1] -> {
                    if (filterList[i].checked){
                        resultToiletList = extractDisableToiletExistingToilet(resultToiletList)
                    }
                }
                nameList[2] -> {
                    if (filterList[i].checked){
                        resultToiletList = extractEmergencyBellExistingToilet(resultToiletList)
                    }
                }
                nameList[3] -> {
                    if (filterList[i].checked){
                        resultToiletList = extractEntranceCCTVExistingToilet(resultToiletList)
                    }
                }
                nameList[4] -> {
                    if (filterList[i].checked){
                        resultToiletList = extractOpenedToilet(resultToiletList)
                    }
                }
                nameList[5] -> {
                    if (filterList[i].checked){
                        resultToiletList = extractPublicToilet(resultToiletList)
                    }
                }
                nameList[6] -> {
                    if (filterList[i].checked){
                        resultToiletList = extractPrivateOwnerToilet(resultToiletList)
                    }
                }
                nameList[7] -> {
                    if (filterList[i].checked){
                        resultToiletList = extractPublicOwnerToilet(resultToiletList)
                    }
                }
            }
        }
        // Init 정보 업데이트
        isFilteredListInit = true
        return resultToiletList.toList()
    }
    /**
     * 조건 필터링 로직
     */

    /**
     * 장애인 소변기가 1개 이상인 데이터 추출
     */
    private fun extractDisableUrinalExistingToilet(toiletList: MutableList<ToiletModel>): MutableList<ToiletModel>{
        val resultList = mutableListOf<ToiletModel>()
        for (i in 0 until toiletList.size){
            if (toiletList[i].male_disabled_urinal_count > 0){
                resultList.add(toiletList[i])
            }
        }
        return resultList
    }
    /**
     * 장애인 대변기가 1개 이상인 데이터 추출
     */
    private fun extractDisableToiletExistingToilet(toiletList: MutableList<ToiletModel>): MutableList<ToiletModel>{
        val resultList = mutableListOf<ToiletModel>()
        for (i in 0 until toiletList.size){
            if (toiletList[i].male_disabled_toilet_count > 0 || toiletList[i].female_disabled_toilet_count > 0){
                resultList.add(toiletList[i])
            }
        }
        return resultList
    }
    /**
     * 비상벨 있는 데이터 추출
     */
    private fun extractEmergencyBellExistingToilet(toiletList: MutableList<ToiletModel>): MutableList<ToiletModel>{
        val resultList = mutableListOf<ToiletModel>()
        for (i in 0 until toiletList.size){
            if (toiletList[i].emergency_bell_installed == "Y"){
                resultList.add(toiletList[i])
            }
        }
        return resultList
    }
    /**
     * 입구에 CCTV 있는 데이터 추출
     */
    private fun extractEntranceCCTVExistingToilet(toiletList: MutableList<ToiletModel>): MutableList<ToiletModel>{
        val resultList = mutableListOf<ToiletModel>()
        for (i in 0 until toiletList.size){
            if (toiletList[i].restroom_entrance_cctv_installed == "Y"){
                resultList.add(toiletList[i])
            }
        }
        return resultList
    }
    /**
     * 개방화장실 데이터 추출
     */
    private fun extractOpenedToilet(toiletList: MutableList<ToiletModel>): MutableList<ToiletModel>{
        val resultList = mutableListOf<ToiletModel>()
        for (i in 0 until toiletList.size){
            if (toiletList[i].category == "개방화장실"){
                resultList.add(toiletList[i])
            }
        }
        return resultList
    }
    /**
     * 공중화장실 데이터 추출
     */
    private fun extractPublicToilet(toiletList: MutableList<ToiletModel>): MutableList<ToiletModel>{
        val resultList = mutableListOf<ToiletModel>()
        for (i in 0 until toiletList.size){
            if (toiletList[i].category == "공중화장실"){
                resultList.add(toiletList[i])
            }
        }
        return resultList
    }
    /**
     * 민간소유 화장실 데이터 추출
     */
    private fun extractPrivateOwnerToilet(toiletList: MutableList<ToiletModel>): MutableList<ToiletModel>{
        val resultList = mutableListOf<ToiletModel>()
        for (i in 0 until toiletList.size){
            if (toiletList[i].restroom_ownership_type.contains("민간소유")){
                resultList.add(toiletList[i])
            }
        }
        return resultList
    }
    /**
     * 공공기관 화장실 데이터 추출
     */
    private fun extractPublicOwnerToilet(toiletList: MutableList<ToiletModel>): MutableList<ToiletModel>{
        val resultList = mutableListOf<ToiletModel>()
        for (i in 0 until toiletList.size){
            if (toiletList[i].restroom_ownership_type.contains("공공기관")){
                resultList.add(toiletList[i])
            }
        }
        return resultList
    }
    /**
     * 도로명 주소로 검색
     */
    private fun getToiletByRoadAddress(
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
    /**
     * 지번 주소로 검색
     */
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
    /**
     * 이름으로 검색
     */
    private fun getToiletByToiletName(
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
            }
        }
        return resultToiletList
    }
    /**
     * 검색 리스트 합치는 함수
     */
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
    /**
     * 1년 이내 체크
     */
    private fun isWithinOneYear(dateStr: String): Boolean {
        return isWithinPeriod(dateStr, 1, PeriodType.YEAR)
    }
    /**
     * 6개월 이내 체크
     */
    private fun isWithinSixMonths(dateStr: String): Boolean {
        return isWithinPeriod(dateStr, 6, PeriodType.MONTH)
    }
    /**
     *  1개월 이내 체크
     */
    private fun isWithinOneMonth(dateStr: String): Boolean {
        return isWithinPeriod(dateStr, 1, PeriodType.MONTH)
    }
    /**
     * 기간 체크를 위한 공통 함수
     */
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
    /**
     * 기간 타입을 위한 enum class
     */
    private enum class PeriodType {
        YEAR, MONTH
    }

}