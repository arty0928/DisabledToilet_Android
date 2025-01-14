package com.dream.disabledtoilet_android.User

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ToiletPostViewModel : ViewModel(){
    private val repository = ToiletPostRepository()
    
    val toiletLikes = MutableLiveData<List<String>>(emptyList()) // 좋아요한 사용자 리스트
    val likeCount = MutableLiveData<Int>(0) // 좋아요 개수
    
    fun observePostLikes(toiletId: Int){
        repository.observePostLikes(toiletId){likes ->
            //null이면 빈 리스트로 설정
            toiletLikes.value = likes ?: emptyList()
            likeCount.value = likes.size
        }
    }

    fun addLike(toiletId : Int, userId : String){
        repository.addLike(toiletId, userId)
    }

    fun removeLike(toiletId: Int, userId: String){
        repository.removeLike(toiletId, userId)
    }

    fun isLikedByUser(userId : String) : Boolean{
        return toiletLikes.value?.contains(userId) == true
    }
}