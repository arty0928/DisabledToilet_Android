package com.dream.disabledtoilet_android.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import com.google.firebase.firestore.DocumentSnapshot

@Parcelize
@Entity(tableName = "toilet_table")
data class ToiletModel(

    var distance : Double = -1.0,

    @PrimaryKey
    val documentId: String = "",
    val number: Int = 0,
    val basis: String = "",
    val restroom_name: String = "",
    val address_road: String = "",
    val address_lot: String = "",
    val male_toilet_count: Int = 0,
    val male_urinal_count: Int = 0,
    val male_child_toilet_count: Int = 0,
    val male_child_urinal_count: Int = 0,
    val female_toilet_count: Int = 0,
    val female_child_toilet_count: Int = 0,
    val management_agency_name: String = "",
    val waste_disposal_method: String = "",
    val safety_management_facility_installed: String = "",
    val emergency_bell_installed: String = "",
    val diaper_change_table_available: String = "",
    val diaper_change_table_location: String = "",
    val data_reference_date: String = "",
    val opening_hours_detail: String = "",
    val opening_hours: String = "",
    val installation_date: String = "",
    val phone_number: String = "",
    val remodeling_date: String = "",
    val wgs84_latitude: Double = 0.0,
    val wgs84_longitude: Double = 0.0,
    // 조건 적용
    val male_disabled_toilet_count: Int = 0,
    val male_disabled_urinal_count: Int = 0,
    val female_disabled_toilet_count: Int = 0,
    // 비상벨
    val emergency_bell_location: String = "",
    // 입구 CCTV
    val restroom_entrance_cctv_installed: String = "",
    // 카테고리
    val category: String = "",
    // 소유권 유형
    val restroom_ownership_type: String = "",
    // 좋아요 수는 Int로 저장하고, MutableLiveData로 감싸기
    var save: Int = 0
) : Parcelable {

    companion object {
        fun fromDocument(document: DocumentSnapshot): ToiletModel {
            return ToiletModel(
                documentId = document.id,
                distance = -1.0,
                number = document.getLong("number")?.toInt() ?: 0,
                category = document.getString("category") ?: "",
                basis = document.getString("basis") ?: "",
                restroom_name = document.getString("toilet_name") ?: "",
                address_road = document.getString("address_road") ?: "",
                address_lot = document.getString("address_lot") ?: "",
                male_toilet_count = document.getLong("male_toilet_count")?.toInt() ?: 0,
                male_urinal_count = document.getLong("male_urinal_count")?.toInt() ?: 0,
                male_disabled_toilet_count = document.getLong("male_disabled_toilet_count")?.toInt() ?: 0,
                male_disabled_urinal_count = document.getLong("male_disabled_urinal_count")?.toInt() ?: 0,
                male_child_toilet_count = document.getLong("male_child_toilet_count")?.toInt() ?: 0,
                male_child_urinal_count = document.getLong("male_child_urinal_count")?.toInt() ?: 0,
                female_toilet_count = document.getLong("female_toilet_count")?.toInt() ?: 0,
                female_disabled_toilet_count = document.getLong("female_disabled_toilet_count")?.toInt() ?: 0,
                female_child_toilet_count = document.getLong("female_child_toilet_count")?.toInt() ?: 0,
                management_agency_name = document.getString("management_agency_name") ?: "",
                restroom_ownership_type = document.getString("restroom_ownership_type") ?: "",
                waste_disposal_method = document.getString("waste_disposal_method") ?: "",
                safety_management_facility_installed = document.getString("safety_management_facility_installed") ?: "",
                emergency_bell_installed = document.getString("emergency_bell_installed") ?: "",
                emergency_bell_location = document.getString("emergency_bell_location") ?: "",
                restroom_entrance_cctv_installed = document.getString("restroom_entrance_cctv_installed") ?: "",
                diaper_change_table_available = document.getString("diaper_change_table_available") ?: "",
                diaper_change_table_location = document.getString("diaper_change_table_location") ?: "",
                data_reference_date = document.getString("data_reference_date") ?: "",
                opening_hours_detail = document.getString("opening_hours_detail") ?: "",
                opening_hours = document.getString("opening_hours") ?: "",
                installation_date = document.getString("installation_date") ?: "",
                phone_number = document.get("phone_number")?.toString() ?: "",
                remodeling_date = document.getString("remodeling_date") ?: "",
                wgs84_latitude = document.getDouble("wgs84_latitude") ?: 0.0,
                wgs84_longitude = document.getDouble("wgs84_longitude") ?: 0.0,
                save = document.getLong("save")?.toInt() ?: 0
            )
        }
    }
}
