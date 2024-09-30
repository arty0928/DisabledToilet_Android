package com.example.disabledtoilet_android.User

data class ApiResponse(
    val success : Boolean,
    val data : List<User>,
    val status : Int
)
