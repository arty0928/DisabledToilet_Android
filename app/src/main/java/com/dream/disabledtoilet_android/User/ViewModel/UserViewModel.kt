package com.dream.disabledtoilet_android.User.ViewModel

import User
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dream.disabledtoilet_android.User.UserRepository

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository()
    val currentUser = MutableLiveData<User?>()

    fun fetchUserByEmail(email: String) {
        repository.loadUser(email) { user ->
            currentUser.value = user
        }
    }
}
