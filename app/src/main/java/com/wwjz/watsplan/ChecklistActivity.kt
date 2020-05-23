package com.wwjz.watsplan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class ChecklistActivity : AppCompatActivity() {

    //Get DB
    val db = FirebaseFirestore.getInstance()
    var major = Major()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checklist)

        //get intent
        val s = intent.getStringExtra("Save")
        val m = intent.getStringExtra("Major")

        Log.d("ccc", m.toString())


        if (s != null) {
            //Load save data
        } else if (m != null) {

            //Query for Major
            val docRef = db.collection("/Majors/").document(m.toString())
            docRef.get().addOnSuccessListener { documentSnapshot ->
                major = documentSnapshot.toObject(Major::class.java)!!
                Log.d("ccc", major.toString())
            }

        } else {
            Log.d("error", "empty intent")
        }

    }
}
