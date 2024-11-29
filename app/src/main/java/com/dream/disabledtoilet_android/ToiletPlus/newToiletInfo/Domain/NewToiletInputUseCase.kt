package com.dream.disabledtoilet_android.ToiletPlus.newToiletInfo.Domain

import ToiletModel
import com.dream.disabledtoilet_android.Utility.Firebase.FirebaseToiletService

/**
 *      새로운 화장실 등록을 위한 도메인 레이어
 */
class NewToiletInputUseCase() {
    /**
     *      새로운 화장실을 등록하는 경우 사용, return isSuccess
     */
    suspend fun registerToilet(toiletModel: ToiletModel): Boolean{
        var isSuccess: Boolean = false
        val firebaseToiletService = FirebaseToiletService()
        // 겹치는 데이터 확인
        val isOverlap = firebaseToiletService.checkToiletOverlap(toiletModel)
        // 겹치는 데이터 없을 때
        if (!isOverlap){
            // 화장실 등록
            isSuccess = firebaseToiletService.uploadNewToiletStatusToFirebase(toiletModel)
        }
        return isSuccess
    }
}