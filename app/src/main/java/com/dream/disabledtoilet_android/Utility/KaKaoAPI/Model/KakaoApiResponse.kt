package com.dream.disabledtoilet_android.Utility.KaKaoAPI.Model

/**
 * Response 받는 Data Class
 */
data class KakaoApiResponse<T>(
    val documents: List<T>,
    val meta: Meta
)
/**
 * 주소 내용 받는 Data Class
 */
data class AddressDocument(
    val address: Address?,
    val road_address: RoadAddress?
)
/**
 * 주소 Data Class
 */
data class Address(
    val address_name: String,
    val region_1depth_name: String,
    val region_2depth_name: String,
    val region_3depth_name: String,
    val mountain_yn: String,
    val main_address_no: String,
    val sub_address_no: String
)
/**
 * 도로명 주소
 */
data class RoadAddress(
    val address_name: String,
    val region_1depth_name: String,
    val region_2depth_name: String,
    val region_3depth_name: String,
    val road_name: String,
    val underground_yn: String,
    val main_building_no: String,
    val sub_building_no: String,
    val building_name: String,
    val zone_no: String
)
/**
 * 도로명, 지번 주소 모두 넣는 모델
 */
data class AddressNameModel(
    val roadAddressName: String,
    val lotAddressName: String
)
/**
 * API 메타데이터
 */
data class Meta(
    val total_count: Int
)