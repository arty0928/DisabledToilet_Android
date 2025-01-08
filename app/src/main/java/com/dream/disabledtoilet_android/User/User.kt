package com.dream.disabledtoilet_android.User

import androidx.annotation.Keep

@Keep
data class User(

    val email: String = "",
    val name: String ="",
    val photoURL: String ="",
    //해당 화장실 번호만
    var likedToilets: List<Int> = mutableListOf(),
    var recentlyViewedToilets: MutableList<Int> = mutableListOf(),
    var registedToilets: MutableList<Int> = mutableListOf()

)
