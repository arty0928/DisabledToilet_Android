package com.example.disabledtoilet_android.ToiletSearch.Model

data class ToiletModel(
    val number: Int = 0,
    val category: String = "",
    val basis: String = "",
    val restroom_name: String = "",
    val address_road: String = "",
    val address_lot: String = "",

    val male_toilet_count: Int = 0,
    val male_urinal_count: Int = 0,

    val male_disabled_toilet_count: Int = 0,
    val male_disabled_urinal_count: Int = 0,

    val male_child_toilet_count: Int = 0,
    val male_child_urinal_count: Int = 0,

    val female_toilet_count: Int = 0,
    val female_disabled_toilet_count: Int = 0,
    val female_child_toilet_count: Int = 0,

    val management_agency_name: String = "",
    val restroom_ownership_type: String = "",
    val waste_disposal_method: String = "",
    val safety_management_facility_installed: String = "",
    val emergency_bell_installed: String = "",
    val emergency_bell_location: String = "",
    val restroom_entrance_cctv_installed: String = "",
    val diaper_change_table_available: String = "",
    val diaper_change_table_location: String = "",
    val data_reference_date: String = "",

    val opening_hours_detail: String = "",
    val opening_hours: String = "",

    val installation_date: String = "",
    val phone_number: String = "",
    val remodeling_date: String =  "",
    val wgs84_latitude: Double = 0.0,
    val wgs84_longitude: Double = 0.0
)

