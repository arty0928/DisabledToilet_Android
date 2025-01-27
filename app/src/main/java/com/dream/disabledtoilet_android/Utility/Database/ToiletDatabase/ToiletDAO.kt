package com.dream.disabledtoilet_android.Utility.Database.ToiletDatabase

import com.dream.disabledtoilet_android.ToiletSearch.Model.ToiletModel
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ToiletDAO {
    @Insert
    suspend fun insertToilet(toilet: ToiletModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(toilets: List<ToiletModel>)

    @Delete
    suspend fun deleteToilet(toilet: ToiletModel)

    @Query("SELECT * FROM toilet_table")
    suspend fun getAllToilets(): List<ToiletModel>
}