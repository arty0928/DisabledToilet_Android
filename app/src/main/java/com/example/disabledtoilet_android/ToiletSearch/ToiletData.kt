package com.example.disabledtoilet_android.ToiletSearch

import android.util.Log
import com.example.disabledtoilet_android.ToiletSearch.Model.ToiletModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object ToiletData {
    val Tag = "[ToiletData]"
    val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://dreamhyoja-default-rtdb.asia-southeast1.firebasedatabase.app")
    val toiletsRef: DatabaseReference = database.getReference("public_toilet")
    var toilets = mutableListOf<ToiletModel>()
    var toiletListInit = false

    fun getToiletData(callback: (List<ToiletModel>?) -> Unit) {
        Log.d(Tag, "getToiletData called")
        toiletsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    Log.d(Tag, childSnapshot.toString())

                    // 각 필드를 개별적으로 가져와서 처리
                    val number = childSnapshot.child("number").getValue(Int::class.java) ?: 0
                    val category = childSnapshot.child("category").getValue(String::class.java) ?: ""
                    val basis = childSnapshot.child("basis").getValue(String::class.java) ?: ""
                    val restroom_name = childSnapshot.child("restroom_name").getValue(String::class.java) ?: ""
                    val address_road = childSnapshot.child("address_road").getValue(String::class.java) ?: ""
                    val address_lot = childSnapshot.child("address_lot").getValue(String::class.java) ?: ""

                    val male_toilet_count = childSnapshot.child("male_toilet_count").getValue(Int::class.java) ?: 0
                    val male_urinal_count = childSnapshot.child("male_urinal_count").getValue(Int::class.java) ?: 0
                    val male_disabled_toilet_count = childSnapshot.child("male_disabled_toilet_count").getValue(Int::class.java) ?: 0
                    val male_disabled_urinal_count = childSnapshot.child("male_disabled_urinal_count").getValue(Int::class.java) ?: 0
                    val male_child_toilet_count = childSnapshot.child("male_child_toilet_count").getValue(Int::class.java) ?: 0
                    val male_child_urinal_count = childSnapshot.child("male_child_urinal_count").getValue(Int::class.java) ?: 0
                    val female_toilet_count = childSnapshot.child("female_toilet_count").getValue(Int::class.java) ?: 0
                    val female_disabled_toilet_count = childSnapshot.child("female_disabled_toilet_count").getValue(Int::class.java) ?: 0
                    val female_child_toilet_count = childSnapshot.child("female_child_toilet_count").getValue(Int::class.java) ?: 0

                    val management_agency_name = childSnapshot.child("management_agency_name").getValue(String::class.java) ?: ""
                    val restroom_ownership_type = childSnapshot.child("restroom_ownership_type").getValue(String::class.java) ?: ""
                    val waste_disposal_method = childSnapshot.child("waste_disposal_method").getValue(String::class.java) ?: ""
                    val safety_management_facility_installed = childSnapshot.child("safety_management_facility_installed").getValue(String::class.java) ?: ""
                    val emergency_bell_installed = childSnapshot.child("emergency_bell_installed").getValue(String::class.java) ?: ""
                    val emergency_bell_location = childSnapshot.child("emergency_bell_location").getValue(String::class.java) ?: ""
                    val restroom_entrance_cctv_installed = childSnapshot.child("restroom_entrance_cctv_installed").getValue(String::class.java) ?: ""
                    val diaper_change_table_available = childSnapshot.child("diaper_change_table_available").getValue(String::class.java) ?: ""
                    val diaper_change_table_location = childSnapshot.child("diaper_change_table_location").getValue(String::class.java) ?: ""
                    val data_reference_date = childSnapshot.child("data_reference_date").getValue(String::class.java) ?: ""

                    val opening_hours_detail = childSnapshot.child("opening_hours_detail").getValue(String::class.java) ?: ""
                    val opening_hours = childSnapshot.child("opening_hours").getValue(String::class.java) ?: ""

                    val installation_date = childSnapshot.child("installation_date").getValue(String::class.java) ?: ""

                    // phone_number는 Long으로 저장된 경우도 처리
                    val phoneNumber = when (val phoneData = childSnapshot.child("phone_number").value) {
                        is Long -> phoneData.toString() // Long을 String으로 변환
                        is String -> phoneData
                        else -> ""
                    }

                    val remodeling_date = childSnapshot.child("remodeling_date").getValue(String::class.java) ?: ""
                    val wgs84_latitude = childSnapshot.child("wgs84_latitude").getValue(Double::class.java) ?: 0.0
                    val wgs84_longitude = childSnapshot.child("wgs84_longitude").getValue(Double::class.java) ?: 0.0

                    // ToiletModel에 필드 값들을 넣어서 객체 생성
                    val toilet = ToiletModel(
                        number,
                        category,
                        basis,
                        restroom_name,
                        address_road,
                        address_lot,
                        male_toilet_count,
                        male_urinal_count,
                        male_disabled_toilet_count,
                        male_disabled_urinal_count,
                        male_child_toilet_count,
                        male_child_urinal_count,
                        female_toilet_count,
                        female_disabled_toilet_count,
                        female_child_toilet_count,
                        management_agency_name,
                        restroom_ownership_type,
                        waste_disposal_method,
                        safety_management_facility_installed,
                        emergency_bell_installed,
                        emergency_bell_location,
                        restroom_entrance_cctv_installed,
                        diaper_change_table_available,
                        diaper_change_table_location,
                        data_reference_date,
                        opening_hours_detail,
                        opening_hours,
                        installation_date,
                        phoneNumber,
                        remodeling_date,
                        wgs84_latitude,
                        wgs84_longitude
                    )

                    toilets.add(toilet)
                }
                toiletListInit = true
                callback(toilets)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(Tag, "getToiletData failed")
                callback(emptyList())
            }
        })
    }

    fun getToilets() {
        Log.d("size of toiletList", toilets.size.toString())
        Log.d("data of toilet", toilets.toString())
    }
}
