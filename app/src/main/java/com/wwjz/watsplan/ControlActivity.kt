package com.wwjz.watsplan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_control.*

class ControlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        submitButton.setOnClickListener{
            submit()
        }
    }


    fun submit() {
        val db = FirebaseFirestore.getInstance()
        val major = hashMapOf(
            "Requirements" to editThis.text.toString().lines().toList()
        )

        db.collection("Majors")
            .document(name.text.toString())
            .set(major)
            .addOnSuccessListener {
                Snackbar.make(submitButton,"DocumentSnapshot successfully written!",Snackbar.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Snackbar.make(submitButton,"Error writing document",Snackbar.LENGTH_LONG).show()
            }

    }
}
