package com.dream.disabledtoilet_android.Utility.Database.PlaceToiletGroupDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dream.disabledtoilet_android.Model.PlaceToiletGroupModel

@Dao
interface PlaceToiletGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(placeToiletGroup: PlaceToiletGroupModel)

    @Update
    suspend fun update(placeToiletGroup: PlaceToiletGroupModel)

    @Delete
    suspend fun delete(placeToiletGroup: PlaceToiletGroupModel)

    @Query("SELECT * FROM place_toilet_group_database WHERE groupId = :groupId")
    suspend fun getGroupById(groupId: String): PlaceToiletGroupModel?

    @Query("SELECT * FROM place_toilet_group_database WHERE toiletId = :toiletId")
    suspend fun getGroupByToiletId(toiletId: String): List<PlaceToiletGroupModel>?

    @Query("SELECT * FROM place_toilet_group_database WHERE placeId = :placeId")
    suspend fun getGroupByPlaceId(placeId: String): List<PlaceToiletGroupModel>?

    @Query("SELECT * FROM place_toilet_group_database WHERE placeId = :placeId AND toiletId = :toiletId")
    suspend fun getGroupByPlaceAndToiletId(placeId: String, toiletId: String): List<PlaceToiletGroupModel>?

    @Query("SELECT * FROM place_toilet_group_database")
    suspend fun getAllGroups(): List<PlaceToiletGroupModel>
}