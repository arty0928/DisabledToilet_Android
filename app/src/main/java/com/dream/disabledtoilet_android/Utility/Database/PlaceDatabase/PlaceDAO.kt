package com.dream.disabledtoilet_android.Utility.Database.PlaceDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dream.disabledtoilet_android.ToiletSearch.Model.ToiletModel
import com.dream.disabledtoilet_android.Utility.KaKaoAPI.Model.SearchResultDocument

@Dao
interface PlaceDAO {
    @Insert
    suspend fun insertPlace(place: SearchResultDocument)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(placeList: List<SearchResultDocument>)

    @Delete
    suspend fun deletePlace(place: SearchResultDocument)

    @Query("SELECT * FROM place_database")
    suspend fun getAllPlaces(): List<SearchResultDocument>
}