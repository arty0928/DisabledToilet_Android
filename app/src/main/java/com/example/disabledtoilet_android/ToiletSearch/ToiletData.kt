package com.example.disabledtoilet_android.ToiletSearch

import android.util.Log
import com.example.disabledtoilet_android.ToiletSearch.Model.ToiletModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object ToiletData {
    val Tag = "[ToiletData]"
    val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://dreamhyoja-default-rtdb.asia-southeast1.firebasedatabase.app")
    val toiletsRef: DatabaseReference = database.getReference("public_toilet")
    var toilets = mutableListOf<ToiletModel>()
    var toiletListInit = false

    fun getToiletData(callback: (List<ToiletModel>?) -> Unit){
        Log.d(Tag, "getToiletData called")
        toiletsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    Log.d(Tag, childSnapshot.toString())
                    val toilet = childSnapshot.getValue(ToiletModel::class.java)

                    toilet?.let { toilets.add(it) }
                }
                toiletListInit = true
                callback(toilets)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(Tag, "getToiletData failed")
                callback(emptyList())
            }
        })
    }

    fun getToilets(){
        Log.d("size of toiletList", toilets.size.toString())
        Log.d("data of toilet", toilets.toString())
    }
}