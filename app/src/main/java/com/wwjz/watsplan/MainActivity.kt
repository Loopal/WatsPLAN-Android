package com.wwjz.watsplan

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.Task
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.Source


data class Major(
    val Communication1: String? = null,
    val Communication2: String? = null,
    val mFixed: String? = null,
    val mFlexible: String? = null,
    val Fixed: String? = null
)

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = FirebaseFirestore.getInstance()

        val docRef2 = db.collection("/Index/")

        docRef2.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("qq", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("qq", "Error getting documents: ", exception)
            }



        val docRef = db.collection("/Majors/").document("Bachelor of Computer Science (BCS)")

        // Source can be CACHE, SERVER, or DEFAULT.
        val source = Source.CACHE

        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Document found in the offline cache
                val document = task.result
                Log.d("abc", "Cached document data: ${document?.data}")
            } else {
                Log.d("abc", "Cached get failed: ", task.exception)
            }
        }

        docRef.get().addOnSuccessListener { documentSnapshot ->
            val m = documentSnapshot.toObject(Major::class.java)
            Log.d("ccc", m.toString())
        }


        val majors = arrayOf("Item 1", "Item 2", "Item 3", "Item 4")

        val adapter = ArrayAdapter<String>(
            this,
            R.layout.dropdown_menu_popup_item,
            majors
        )

        filled_exposed_dropdown.setAdapter(adapter)

        createButton.setOnClickListener{

            val intent = Intent()
            intent.setClass(this, ControlActivity::class.java)
            startActivity(intent)

            if (createButton.currentTextColor == Color.BLACK) {
                createButton.setBackgroundColor(Color.BLACK)
                createButton.setTextColor(resources.getColor(R.color.uwYellow))
            } else {
                createButton.setBackgroundColor(resources.getColor(R.color.uwYellow))
                createButton.setTextColor(Color.BLACK)

            }

        }

        updateDepthRequire.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, DepthControlActivity::class.java)
            startActivity(intent)
        }
    }

}