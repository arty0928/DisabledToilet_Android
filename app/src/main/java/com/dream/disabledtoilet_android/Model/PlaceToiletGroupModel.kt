package com.dream.disabledtoilet_android.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place_toilet_group_database")
data class PlaceToiletGroupModel (
    @PrimaryKey(autoGenerate = false)
    val groupId: String,                      // 장소 ID
    @ColumnInfo(name = "placeId")
    val placeId: String,              // 장소명, 업체명
    @ColumnInfo(name = "toiletId")
    val toiletId: String,           // 카테고리 이름
)