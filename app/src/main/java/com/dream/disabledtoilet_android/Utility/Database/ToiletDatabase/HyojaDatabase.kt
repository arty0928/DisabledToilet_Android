package com.dream.disabledtoilet_android.Utility.Database.ToiletDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dream.disabledtoilet_android.Model.ToiletModel
import androidx.room.TypeConverters
import com.dream.disabledtoilet_android.Model.PlaceModel
import com.dream.disabledtoilet_android.Model.PlaceToiletGroupModel
import com.dream.disabledtoilet_android.Utility.Database.Converters
import com.dream.disabledtoilet_android.Utility.Database.PlaceDatabase.PlaceDAO
import com.dream.disabledtoilet_android.Utility.Database.PlaceToiletGroupDatabase.PlaceToiletGroupDao

@Database(entities = [PlaceModel::class, ToiletModel::class, PlaceToiletGroupModel::class], version = 1)
@TypeConverters(Converters::class) // Type Converter 등록
abstract class HyojaDatabase : RoomDatabase() {
    abstract fun toiletDao(): ToiletDAO
    abstract fun placeDao(): PlaceDAO
    abstract fun placeToiletGroupDao(): PlaceToiletGroupDao

    companion object {
        @Volatile
        private var INSTANCE: HyojaDatabase? = null

        fun getDatabase(context: Context): HyojaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HyojaDatabase::class.java,
                    "hyoja_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
