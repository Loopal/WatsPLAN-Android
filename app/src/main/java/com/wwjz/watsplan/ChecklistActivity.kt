package com.wwjz.watsplan

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
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

        //toggle buttons
        toggleGroup.setOnCheckedChangeListener { radioGroup, i ->
            for (b in radioGroup.children) {
                var tb = b as ToggleButton
                if( b.id == i) {
                    tb.isChecked = true
                    tb.setTextColor(Color.BLACK)
                    tb.setBackgroundColor(getResources().getColor(R.color.uwYellow))
                } else {
                    tb.isChecked = false
                    tb.setTextColor(getResources().getColor(R.color.uwYellow))
                    tb.setBackgroundColor(Color.BLACK)
                }
            }
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
        model.storedCards.add(Card("SELECT 1 FROM", false, 1, commItem1))

        val commItem2 = mutableListOf<String>()
        for (item in major.Communication2!!) {
            commItem2.add(item)
        }
        model.storedCards.add(Card("SELECT 1 FROM", false, 1, commItem2))


        for (item in major.sFixed!!) {
            val temp = mutableListOf<String>()
            for (entry in item.split(",").toList()) {
                temp.add(entry)
            }
            model.storedCards.add(Card("SELECT 1 FROM", false, 1, temp))
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
            model.storedCards.add(Card("SELECT ${num} FROM", false, num, temp))
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
            model.storedCards.add(Card("SELECT ${num} FROM", false, num, temp))
        }

        model.cards.addAll(model.storedCards)
        newAdapter.notifyDataSetChanged()
    }

    fun toggleFilter(v : View) {
        toggleGroup.check(v.id)
        Log.d("id", v.id.toString())
        when (v.id) {
            selectAll.id -> newAdapter.applyFilter(0,101)
            selectChecked.id -> newAdapter.applyFilter(100,101)
            selectUnchecked.id -> newAdapter.applyFilter(0,99)
        }
    }

}
