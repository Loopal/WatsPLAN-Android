package com.wwjz.watsplan

import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_checklist.*

class ChecklistActivity : AppCompatActivity() {

    //Get DB
    val db = FirebaseFirestore.getInstance()
    var major = Major()
    var model = Model.mInstance
    var newAdapter = cardRecyclerAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checklist)

        model.cards.clear()
        model.storedCards.clear()

        //get intent
        val s = intent.getStringExtra("Save")
        val m = intent.getStringExtra("Major")


        if (s != null) {
            //Load save data
        } else if (m != null) {

            //Query for Major
            val docRef = db.collection("/Majors/").document(m.toString())
            docRef.get().addOnSuccessListener { documentSnapshot ->
                major = documentSnapshot.toObject(Major::class.java)!!
                updateCards()
            }

        } else {
            Log.d("error", "empty intent")
        }


        //recycler view
        cardRecycler.layoutManager = LinearLayoutManager(this)
        newAdapter = cardRecyclerAdapter(this)
        cardRecycler.adapter = newAdapter
    }

    private fun updateCards() {
        val commItem1 = mutableListOf<String>()
        for (item in major.Communication1!!) {
            commItem1.add(item)
        }
        model.storedCards.add(Card("SELECT 1 FROM", false, commItem1))

        val commItem2 = mutableListOf<String>()
        for (item in major.Communication2!!) {
            commItem2.add(item)
        }
        model.storedCards.add(Card("SELECT 1 FROM", false, commItem2))


        for (item in major.sFixed!!) {
            val temp = mutableListOf<String>()
            for (entry in item.split(",").toList()) {
                temp.add(entry)
            }
            model.storedCards.add(Card("SELECT 1 FROM", false, temp))
        }


        for (item in major.mFixed!!) {
            val temp = mutableListOf<String>()
            var num = 0
            for (entry in item.split(",").toList()) {
                if (entry.length == 1) {
                    num = entry.toInt()
                    continue
                } else {
                    temp.add(entry)
                }
            }
            model.storedCards.add(Card("SELECT ${num} FROM", false, temp))
        }


        for (item in major.mFlexible!!) {
            val temp = mutableListOf<String>()
            var num = 0
            for (entry in item.split(",").toList()) {
                if (entry.length == 1) {
                    num = entry.toInt()
                    continue
                } else {
                    temp.add(entry)
                }
            }
            model.storedCards.add(Card("SELECT ${num} FROM", false, temp))
        }

        model.cards.addAll(model.storedCards)
        newAdapter.notifyDataSetChanged()
    }

}
