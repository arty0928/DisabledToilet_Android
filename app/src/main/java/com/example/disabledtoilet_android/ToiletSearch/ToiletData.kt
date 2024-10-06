package com.example.disabledtoilet_android.ToiletSearch

import android.util.Log
import com.example.disabledtoilet_android.ToiletSearch.Model.ToiletModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object ToiletData {
    var toilets = mutableListOf<ToiletModel>()

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val toiletsRef: DatabaseReference = database.getReference("public_toilet")

    fun getToiletData(callback: (List<ToiletModel>?) -> Unit){
        toiletsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val toilet = childSnapshot.getValue(ToiletModel::class.java)
                    toilet?.let { toilets.add(it) }
                }
                callback(toilets)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error: ${error.message}")
                callback(emptyList())
            }
        })
    }

    fun getToilets(){
        Log.d("size of toiletList", toilets.size.toString())
        Log.d("data of toilet", toilets.toString())
    }
}