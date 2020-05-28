package com.wwjz.watsplan

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_checklist.*
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream


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
        val f = intent.getStringExtra("Faculty")
        val o = intent.getStringExtra("Option")

        Log.d("asd",m.toString())
        Log.d("asd",o.toString())


        if (s != null) {
            //Load save data
        } else if (m != null) {
            //Query for Major
            setlogo(f)
            if (o != null) {
                majorName.text = m + " | " +o
                val docRef = db.collection("/Majors/").document("$m | $o")
                docRef.get().addOnSuccessListener { documentSnapshot ->
                    major = documentSnapshot.toObject(Major::class.java)!!
                    updateCards()
                }
            } else {
                majorName.text = m
                val docRef = db.collection("/Majors/").document(m.toString())
                docRef.get().addOnSuccessListener { documentSnapshot ->
                    major = documentSnapshot.toObject(Major::class.java)!!
                    updateCards()
                }
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
            val temp = item.split(";").toList()
            model.storedCards.add(Card(temp[0], false, temp[1].toInt(), temp.subList(2,temp.size)))
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

    fun setlogo(s : String) {
        when(s) {
            "Applied Health Sciences"-> facultyLogo.setImageDrawable(getDrawable(R.drawable.ahs_logo))
            "Arts"-> facultyLogo.setImageDrawable(getDrawable(R.drawable.arts_logo))
            "Engineering"-> facultyLogo.setImageDrawable(getDrawable(R.drawable.eng_logo))
            "Environment"-> facultyLogo.setImageDrawable(getDrawable(R.drawable.env_logo))
            "Mathematics"-> facultyLogo.setImageDrawable(getDrawable(R.drawable.math_logo))
            "Science"-> facultyLogo.setImageDrawable(getDrawable(R.drawable.sci_logo))
        }
    }

    fun saveChecklist(v : View) {

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        AlertDialog.Builder(this)
            .setTitle("Save")
            .setMessage("Save your current checklist")
            .setView(input)
            .setNegativeButton("cancel") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("confirm") { dialog, which ->
                val curText = input.getText().toString()
                if (curText != "") {
                    val f = File(this.getDir("saves", Context.MODE_PRIVATE), "$curText.save")
                    f.createNewFile()
                    f.printWriter().use {
                        for (c in model.storedCards) {
                            var temp = ""
                            temp += c.text + "?"
                            temp += c.done.toString() + "?"
                            temp += c.checkedBoxes.joinToString(separator = ";") + "?"
                            temp += c.num.toString() + "?"
                            temp += c.progress.toString() + "?"
                            temp += c.items.joinToString(separator = ";")
                            it.println(temp)
                        }
                    }
                    val l = File(this.getDir("saves", Context.MODE_PRIVATE), "$curText.save").readLines()
                    for (ll in l) {
                        Log.d("asd",ll)
                    }

                }
            }
            .show()
    }

    fun loadChecklist(s : String){
        val lines = File(this.getDir("saves", Context.MODE_PRIVATE), "$s.save").readLines()
        model.storedCards.clear()
        model.cards.clear()
        for(ll in lines) {
            val temp = ll.split("?")
            val curCard = Card(temp[0], temp[1].toBoolean(),temp[3].toInt(), temp[5].split(";").toList())
            curCard.progress = temp[4].toInt()
            curCard.checkedBoxes = temp[2].split(";").map{it.toInt() }.toMutableList()
            model.storedCards.add(curCard)
            model.cards.addAll(model.storedCards)
            newAdapter.notifyDataSetChanged()
        }

    }


}
