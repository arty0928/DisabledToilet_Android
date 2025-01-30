package com.dream.disabledtoilet_android.Utility.Database.PlaceDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dream.disabledtoilet_android.Model.PlaceModel

@Dao
interface PlaceDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(placeList: List<PlaceModel>)

    @Delete
    suspend fun deletePlace(place: PlaceModel)

    @Query("SELECT * FROM place_database")
    suspend fun getAllPlaces(): List<PlaceModel>
}