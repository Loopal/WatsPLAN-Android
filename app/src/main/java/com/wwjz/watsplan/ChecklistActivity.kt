package com.wwjz.watsplan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class ChecklistActivity : AppCompatActivity() {

    var major = Major()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checklist)

        //get intent
        val s = intent.getStringArrayExtra("Save")
        val m = intent.getStringArrayExtra("Major")

        if (s != null) {
            //Load save data
        } else if (m != null) {
            //Get DB
            val db = FirebaseFirestore.getInstance()
            //Query for Major
            val docRef = db.collection("/Majors/").document()
            docRef.get().addOnSuccessListener { documentSnapshot ->
                major = documentSnapshot.toObject(Major::class.java)!!
                Log.d("ccc", major.toString())
            }

        } else {
            Log.d("error", "empty intent")
        }

    }
}
