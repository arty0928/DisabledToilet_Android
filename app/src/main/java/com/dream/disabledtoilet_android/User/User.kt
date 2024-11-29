package com.dream.disabledtoilet_android.User

import ToiletModel

data class User(

    val email : String,
    val name : String,
    val photoURL : String,

    var likedToilets : MutableList<ToiletModel>  = mutableListOf(),
    var recentlyViewedToilets : MutableList<ToiletModel> = mutableListOf(),

    var registedToilets : MutableList<ToiletModel> = mutableListOf()

)
