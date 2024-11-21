package com.example.disabledtoilet_android.Utility.Firebase

import ToiletModel
import android.util.Log
import androidx.concurrent.futures.await
import com.google.firebase.database.FirebaseDatabase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseToiletService {
    private val firebaseUrl = "https://dreamhyoja-default-rtdb.asia-southeast1.firebasedatabase.app"
    /**
     *     ToiletModel을 통해서 Firebase에 데이터를 업로드
     */
    suspend fun uploadNewToiletStatusToFirebase(newToiletModel: ToiletModel): Boolean {
        return try {
            val database: FirebaseDatabase = FirebaseDatabase.getInstance(firebaseUrl)
            val toiletRef = database.getReference("toilets").push() // 새로운 키 생성

            // suspendCoroutine을 사용하여 콜백을 코루틴으로 변환
            suspendCoroutine { continuation ->
                toiletRef.setValue(newToiletModel)
                    .addOnSuccessListener {
                        Log.d("Firebase", "New toilet status uploaded successfully")
                        continuation.resume(true)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firebase", "Failed to upload new toilet status", exception)
                        continuation.resume(false)
                    }
            }
        } catch (exception: Exception) {
            Log.e("Firebase", "Failed to upload new toilet status", exception)
            false // 업로드 실패 시 false 반환
        }
    }

    /**
     *      FireBase에 겹치는 데이터가 있는지 확인
     */
    suspend fun checkToiletOverlap(newToiletModel: ToiletModel): Boolean {
        val isOverlap: Boolean = true
        val database: FirebaseDatabase =
            FirebaseDatabase.getInstance(firebaseUrl)
        val toiletRef = database.getReference("toilets")
        //TODO("겹치는 데이터 확인 로직")
        return isOverlap
    }
}