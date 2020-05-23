package com.wwjz.watsplan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.android.synthetic.main.activity_depth_control.*

class DepthControlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_depth_control)

        writeToDepthButton.setOnClickListener{
            writeToDepthDataBase()
        }
    }


    private fun writeToDepthDataBase() {
        val db = FirebaseFirestore.getInstance()

        val myDepthRequirement = hashMapOf(
            "Hum" to Hum.text.toString().split(";").toList(),
            "SS" to SS.text.toString().split(";").toList(),
            "PS" to PS.text.toString().split(";").toList(),
            "PAS" to PAS.text.toString().split(";").toList()
        )

        db.collection("Breadth_and_Depth")
            .document("All")
            .set(myDepthRequirement)
            .addOnSuccessListener {
                Log.d("sss", "Breadth and Depth Requirement successfully written!")
                Snackbar.make(writeToDepthButton,"Breadth and Depth Requirement successfully written!", Snackbar.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Log.w("sss", "Error writing document", e)
                Snackbar.make(writeToDepthButton,"Error writing document", Snackbar.LENGTH_LONG).show()
            }

    }

}
