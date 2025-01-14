package com.dream.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.DataLayer.OptionStringList
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.ViewModel.FilterStatus
import com.kakao.vectormap.LatLng
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.ViewModel.FilterViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 화장실 데이터 처리 클래스
 * 조건 검색 다이얼로그에서 필터링하면 해당 클래스에 반영됨(filteredToiletList)
 */
@RequiresApi(Build.VERSION_CODES.O)
class ToiletRepository {
    val Tag = "[ToiletRepository]"
    /**
     * 키워드 검색 함수
     */
    fun getToiletWithSearchKeyword(
        toiletList: List<ToiletModel>,
        keyword: String
    ): MutableList<ToiletModel> {
        val finalResult: MutableList<ToiletModel>
        finalResult = applyQuery(toiletList, keyword)
        return finalResult
    }
    /**
     * 키워드로 검색한 내용들 합침
     */
    private fun applyQuery(
        toiletList: List<ToiletModel>,
        keyword: String
    ): MutableList<ToiletModel> {
        Log.d("test toiletRepo", "들어온 화장실 데이터 수: " + toiletList.size)
        val roadAddressResult = getToiletByRoadAddress(toiletList, keyword)
        val lotAddressResult = getToiletByLotAddress(toiletList, keyword)
        val nameResult = getToiletByToiletName(toiletList, keyword)
        val finalResult = getMergedResults(
            roadAddressResult,
            lotAddressResult,
            nameResult
        )
        Log.d("test toiletRepo", "검색된 화장실 데이터 수: " + finalResult.size.toString())
        return finalResult
    }
    /**
     * 조건적용 다이얼로그 데이터를 반영한 필터 리스트 생성.
     * 필터 세팅되면 바로 실행됨
     * 넘겨받은 toiletList이용해서 fliteredList 생성
     */
    fun setFilteredToiletList(
        filterStatus: FilterStatus,
        toiletList: List<ToiletModel>
    ): List<ToiletModel> {
        //여기에 추가해가는 방식
        var resultToiletList = mutableListOf<ToiletModel>()
        // 최근 점검
        when (filterStatus.recentCheck.value) {
            0 -> {
                resultToiletList = toiletList.toMutableList()
            }
            1 -> {
                for (i in toiletList.indices) {
                    if (isWithinOneYear(toiletList[i].data_reference_date)){
                        resultToiletList.add(toiletList[i])
                    }
                }
            }

            2 -> {
                for (i in toiletList.indices) {
                    if (isWithinSixMonths(toiletList[i].data_reference_date)){
                        resultToiletList.add(toiletList[i])
                    }
                }
            }

            3 -> {
                for (i in toiletList.indices) {
                    if (isWithinOneMonth(toiletList[i].data_reference_date)){
                        resultToiletList.add(toiletList[i])
                    }
                }
            }
        }

        //조건 적용
        val optionList = filterStatus.optionStatus.optionStatusList

        for (i in 0 until optionList.size){
            when(optionList[i].option){
                OptionStringList().disabledUrinal -> {
                    if (optionList[i].isChecked){
                        // resultToiletList에서 데이터 추출
                        resultToiletList = extractDisableUrinalExistingToilet(resultToiletList)
                    }
                }
                OptionStringList().disabledToilet -> {
                    if (optionList[i].isChecked){
                        resultToiletList = extractDisableToiletExistingToilet(resultToiletList)
                    }
                }
                OptionStringList().emergencyBell -> {
                    if (optionList[i].isChecked){
                        resultToiletList = extractEmergencyBellExistingToilet(resultToiletList)
                    }
                }
                OptionStringList().entranceCCTV -> {
                    if (optionList[i].isChecked){
                        resultToiletList = extractEntranceCCTVExistingToilet(resultToiletList)
                    }
                }
            }
        }

        Log.d("test toiletRepo", "필터링된 화장실 리스트 사이즈: " + resultToiletList.size)
        return resultToiletList.toList()
    }
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

    /**
     * 화장시 거리 업데이트
     */
    fun updateDistance(userLocation : LatLng){
        var distanceInMeters: Float = 3F

        if(userLocation != null){

            val currentLatitude = userLocation.latitude
            val currentLongitude = userLocation.longitude
            // 유저 위치
            val currentLocation = Location("").apply {
                latitude = currentLatitude
                longitude = currentLongitude
            }

            for(toilet in ToiletData.cachedToiletList!!){
                // 화장실 위치의 Location 객체 생성
                val toiletLocation = Location("").apply {
                    latitude = toilet.wgs84_latitude
                    longitude = toilet.wgs84_longitude
                }
                // 두 위치 사이의 거리 계산 (미터 단위)
                distanceInMeters = currentLocation.distanceTo(toiletLocation)
                toilet.distance = distanceInMeters.toDouble()
            }
        }
    }

    /**
     * 거리 계산
     */
    fun calculateDistance(toiletData: ToiletModel, userLocation : LatLng): String {
        var formattedDistance: String? = null
        if (userLocation != null) {
            if (toiletData.distance != -1.0) {
                Log.d("Distance", toiletData.distance.toString())

                // 거리를 적절한 형식으로 변환
                formattedDistance = when {
                    toiletData.distance < 1000 -> "${toiletData.distance.toInt()}m"
                    else -> String.format("%.1fkm", toiletData.distance / 1000)
                }
            } else {
                formattedDistance = "-"
            }
        } else {
            Log.d("test calculateDist", "userLocation is null in ToiletListViewAdapter")
            formattedDistance = " - "
        }
        return formattedDistance.toString()
    }

}