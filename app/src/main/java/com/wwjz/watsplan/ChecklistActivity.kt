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
            checklistTitle.text = m

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

        for (item in major.Requirements!!) {
            val temp = item.split(",").toList()
            if (temp.size > 3) {
                model.storedCards.add(Card("Select ${temp[1]} From ${temp[0]}", false, temp[1].toInt(), temp.subList(2,temp.size)))
            } else {
                model.storedCards.add(Card("Select ${temp[0]}", false, temp[1].toInt(), temp.subList(2,temp.size)))
            }

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
