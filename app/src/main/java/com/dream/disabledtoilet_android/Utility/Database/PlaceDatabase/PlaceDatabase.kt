package com.dream.disabledtoilet_android.Utility.Database.PlaceDatabase

import androidx.room.Database
import com.dream.disabledtoilet_android.Utility.KaKaoAPI.Model.SearchResultDocument

abstract class PlaceDatabase {
    abstract fun placeDao(): PlaceDAO
}