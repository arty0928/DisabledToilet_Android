package com.example.disabledtoilet_android.User

import ToiletModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class User(

    val email : String,
    val name : String,
    val photoURL : String,

    val likedToilets : List<ToiletModel>,
    val recentlyViewedToilets : List<ToiletModel>

)
