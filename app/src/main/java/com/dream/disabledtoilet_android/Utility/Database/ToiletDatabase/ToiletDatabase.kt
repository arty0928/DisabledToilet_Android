package com.dream.disabledtoilet_android.Utility.Database.ToiletDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dream.disabledtoilet_android.ToiletSearch.Model.ToiletModel
import androidx.room.TypeConverters
import com.dream.disabledtoilet_android.Utility.Database.Converters

@Database(entities = [ToiletModel::class], version = 1)
@TypeConverters(Converters::class) // Type Converter 등록
abstract class ToiletDatabase : RoomDatabase() {
    abstract fun databaseDao(): ToiletDAO

    companion object {
        @Volatile
        private var INSTANCE: ToiletDatabase? = null

        fun getDatabase(context: Context): ToiletDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToiletDatabase::class.java,
                    "toilet_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
