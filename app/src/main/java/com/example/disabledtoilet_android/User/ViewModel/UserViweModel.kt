package com.example.disabledtoilet_android.User.ViewModel

import ToiletModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.disabledtoilet_android.ToiletSearch.ToiletData
import com.example.disabledtoilet_android.User.User

class UserViweModel : ViewModel() {
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser


    init {
        _currentUser.value = ToiletData.currentUser
    }



    fun updateUser(updatedUser : User){
        _currentUser.value = updatedUser
    }

    /**
     * 최근 본 화장실 추가
     */
    fun addRecentViewToilet(toilet: ToiletModel) {
        val currentUser = _currentUser.value ?: return
        val recentToilets = currentUser.recentlyViewedToilets

        // 이미 있으면 맨 앞으로 이동
        val index = recentToilets.indexOfFirst { it.number == toilet.number }
        if (index != -1) {
            recentToilets.removeAt(index)
        }
        recentToilets.add(0, toilet)

        // 최대 10개 유지
        if (recentToilets.size > 10) {
            recentToilets.removeAt(recentToilets.size - 1)
        }

        // 데이터 업데이트
        currentUser.recentlyViewedToilets = recentToilets
        updateUser(currentUser)
    }


    /**
     * currentUser.saveList에 좋아요한 화장실 추가 및 삭제
     */
    fun toggleLikedToilet(toilet: ToiletModel) {
        val currentUser = _currentUser.value ?: return
        val likedToilets = currentUser.likedToilets

        val index = likedToilets.indexOfFirst { it.number == toilet.number }
        if (index != -1) {
            likedToilets.removeAt(index)
            toilet.save = (toilet.save ?: 0) - 1 // save 값 감소
        } else {
            likedToilets.add(toilet)
            toilet.save = (toilet.save ?: 0) + 1 // save 값 증가
        }

        currentUser.likedToilets = likedToilets
        updateUser(currentUser)
    }

    /**
     * 좋아요 여부 확인
     */
    fun isToiletLiked(toilet: ToiletModel): Boolean {
        val currentUser = _currentUser.value ?: return false
        return currentUser.likedToilets.any { it.number == toilet.number }
    }

    /**
     * 좋아요 리스트를 별도로 LiveData로 제공
     */
    val likedToilets : LiveData<List<ToiletModel>> = MutableLiveData<List<ToiletModel>>().apply{
        _currentUser.observeForever { user ->
            value = user?.likedToilets ?: emptyList()
        }
    }
}