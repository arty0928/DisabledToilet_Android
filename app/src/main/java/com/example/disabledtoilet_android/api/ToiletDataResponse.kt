package com.example.disabledtoilet_android.api

data class ToiletDataResponse(
    val success: Boolean,
    val data: List<ToiletData>,
    val status: Int
)

data class ToiletData(
    val address_lot: String? = null,
    val address_road: String? = null,
    val basis: String? = null,
    val category: String? = null,
    val data_reference_date: String? = null,
    val diaper_change_table_available: String? = null,  // Boolean -> String
    val diaper_change_table_location: String? = null,
    val emergency_bell_installed: String? = null,  // Boolean -> String
    val emergency_bell_location: String? = null,
    val female_child_toilet_count: Int? = null,
    val female_disabled_toilet_count: Int? = null,
    val female_toilet_count: Int? = null,
    val installation_date: String? = null,
    val male_child_toilet_count: Int? = null,
    val male_child_urinal_count: Int? = null,
    val male_disabled_toilet_count: Int? = null,
    val male_disabled_urinal_count: Int? = null,
    val male_toilet_count: Int? = null,
    val male_urinal_count: Int? = null,
    val management_agency_name: String? = null,
    val number: Long? = null,  // String -> Long
    val opening_hours: String? = null,
    val opening_hours_detail: String? = null,
    val phone_number: String? = null,
    val remodeling_date: String? = null,
    val restroom_entrance_cctv_installed: String? = null,  // Boolean -> String
    val restroom_name: String? = null,
    val restroom_ownership_type: String? = null,
    val safety_management_facility_installed: String? = null,  // Boolean -> String
    val waste_disposal_method: String? = null,
    val wgs84_latitude: Double? = null,
    val wgs84_longitude: Double? = null
)
