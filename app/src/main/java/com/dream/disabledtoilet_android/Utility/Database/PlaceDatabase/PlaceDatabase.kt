package com.dream.disabledtoilet_android.Utility.Database.PlaceDatabase


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dream.disabledtoilet_android.Utility.Database.ToiletDatabase.ToiletDatabase
import com.dream.disabledtoilet_android.Utility.KaKaoAPI.Model.SearchResultDocument

@Database(entities = [SearchResultDocument::class], version = 1)
abstract class PlaceDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDAO
    companion object {
        @Volatile
        private var INSTANCE: PlaceDatabase? = null

        fun getDatabase(context: Context): PlaceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlaceDatabase::class.java,
                    "place_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}