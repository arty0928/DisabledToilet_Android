package com.example.disabledtoilet_android.data

import ToiletData
import com.google.gson.annotations.SerializedName

data class ToiletDataResponse(
    val success: Boolean,
    val data: List<ToiletData>,
    val status: Int
)

data class ToiletData(
    val id: Int,
    val category: String,
    val legalBasis: String,
    val toiletName: String,
    val roadAddress: String,
    val lotAddress: String,
    val maleToiletCount: Int,
    val maleUrinalCount: Int,
    val maleDisabledToiletCount: Int,
    val maleDisabledUrinalCount: Int,
    val maleChildToiletCount: Int,
    val maleChildUrinalCount: Int,
    val femaleToiletCount: Int,
    val femaleDisabledToiletCount: Int,
    val femaleChildToiletCount: Int,
    val managingOrganization: String,
    val phoneNumber: String,
    val openingHours: String,
    val detailedOpeningHours: String,
    val installationDate: String,
    val latitude: Double,
    val longitude: Double,
    val ownershipType: String,
    val sewageTreatment: String,
    val safetyFacilityTarget: String,
    val emergencyBell: String,
    val emergencyBellLocation: String,
    val entranceCctv: String,
    val diaperChangingTable: String,
    val diaperChangingTableLocation: String,
    val remodelingDate: String,
    val dataReferenceDate: String
)