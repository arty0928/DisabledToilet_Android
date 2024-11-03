package com.example.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

object ToiletData {
    val Tag = "[ToiletData]"
    val database: FirebaseDatabase =
        FirebaseDatabase.getInstance("https://dreamhyoja-default-rtdb.asia-southeast1.firebasedatabase.app")
    val toiletsRef: DatabaseReference = database.getReference("public_toilet")
    var toilets = mutableListOf<ToiletModel>()
    var toiletListInit = false
    var cachedToiletList: List<ToiletModel>? = null

    fun getToiletAllData(onSuccess: (List<ToiletModel>) -> Unit, onFailure: (Exception) -> Unit) {
        if (cachedToiletList != null) {
            // 캐시된 데이터가 있는 경우
            onSuccess(cachedToiletList!!)
        } else {
            // Firestore에서 데이터 로드
            val db = FirebaseFirestore.getInstance()
            db.collection("dreamhyoja")
                .get()
                .addOnSuccessListener { documents ->
                    cachedToiletList = documents.map { doc ->
                        ToiletModel.fromDocument(doc)
                    }
                    onSuccess(cachedToiletList!!)
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
            toiletListInit = true
        }
    }


    fun getToilets() {
        Log.d("size of toiletList", toilets.size.toString())
        Log.d("data of toilet", toilets.toString())
    }
}
