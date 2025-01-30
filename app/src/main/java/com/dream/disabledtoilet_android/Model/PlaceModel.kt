package com.dream.disabledtoilet_android.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "place_database")
@Parcelize
data class PlaceModel(
    @PrimaryKey(autoGenerate = false)
    val id: String = "my_place",                      // 장소 ID
    val place_name: String = "내 장소",              // 장소명, 업체명
    val category_name: String = "나의 카테고리",           // 카테고리 이름
    val category_group_code: String = "0",     // 중요 카테고리만 그룹핑한 카테고리 그룹 코드
    val category_group_name: String = "0",     // 중요 카테고리만 그룹핑한 카테고리 그룹명
    val phone: String = "0",                   // 전화번호
    val address_name: String = "0",            // 전체 지번 주소
    val road_address_name: String = "0",       // 전체 도로명 주소
    val x: String = "0",                       // X 좌표값 (경도)
    val y: String = "0",                       // Y 좌표값 (위도)
    val place_url: String = "0",               // 장소 상세페이지 URL
    val distance: String? = "0"
) : Parcelable
