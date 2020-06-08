package com.wwjz.watsplan

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_checklist.*
import kotlinx.android.synthetic.main.delete_dialog.*
import kotlinx.android.synthetic.main.edittext_dialog.*
import kotlinx.android.synthetic.main.edittext_dialog.view.*
import java.io.File


class ChecklistActivity : AppCompatActivity() {

    //Get DB
    val db = FirebaseFirestore.getInstance()
    var major = Major()
    var model = Model.mInstance
    var newAdapter = cardRecyclerAdapter(this)
    var facultyName = ""
    var fabExpand = false
    // Create the Cloud Storage for user data
    val storage = FirebaseStorage.getInstance()
    // User Auth
    val fAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checklist)

        //fabs
        fabExpand = false
        menuButton.setOnClickListener {
            when(fabExpand) {
                true->closefab()
                false->openfab()
            }
        }

        model.facultyName = ""
        model.majorName = ""
        model.fileName = ""
        model.cards.clear()
        model.storedCards.clear()
        model.changed = false

        //get intent
        val s = intent.getStringExtra("Save")
        val m = intent.getStringExtra("Major")
        val f = intent.getStringExtra("Faculty")
        val o = intent.getStringExtra("Option")

        if(f != null) {
            model.facultyName = f
        }

        if (s != null) {
            //Load save data
            loadChecklist(s)
        } else if (m != null) {
            //Query for Major
            setlogo(f)
            if (o != null) {
                model.majorName = m + " | " +o
                majorName.text = m + " | " +o
                val docRef = db.collection("/Majors/").document("$m | $o")
                docRef.get().addOnSuccessListener { documentSnapshot ->
                    major = documentSnapshot.toObject(Major::class.java)!!
                    updateCards()
                }
            } else {
                model.majorName = m
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

    override fun onBackPressed() {

        if(model.changed){
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.edittext_dialog, null)
            val mAlertDialog = AlertDialog.Builder(this).setView(mDialogView).show()
            if (model.fileName == "") {
                mDialogView.dialogTextField.visibility = View.VISIBLE
                mDialogView.dialog_desc.text = "Create a new save file"
            } else {
                mDialogView.dialogTextField.visibility = View.GONE
                mDialogView.dialog_desc.text = "Overwrite save file ${model.fileName}?"
            }

            mAlertDialog.edit_dialog_cancel.setOnClickListener {
                mAlertDialog.dismiss()
                finish()
            }

            mAlertDialog.edit_dialog_confirm.setOnClickListener {
                var curText = model.fileName
                if (curText == "") {
                    curText = mAlertDialog.dialogEditText.text.toString()
                }
                if (curText != "") {
                    try {
                        val f = File(this.getDir("saves", Context.MODE_PRIVATE), "$curText.save")
                        f.createNewFile()
                        f.printWriter().use {
                            it.println(model.facultyName)
                            it.println(model.majorName)
                            for (c in model.storedCards) {
                                var temp = ""
                                temp += c.text + "?"
                                temp += c.done.toString() + "?"
                                temp += c.checkedBoxes.joinToString(separator = ";") + "?"
                                temp += c.num.toString() + "?"
                                temp += c.progress.toString() + "?"
                                temp += c.items.joinToString(separator = ";") + "?"
                                temp += c.comment
                                it.println(temp)
                            }
                        }
                        mAlertDialog.dismiss()
                        finish()
                    } catch(e : Exception){
                        Snackbar.make(mDialogView,"Unable to create/update save file",
                            Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.BLACK)
                            .setTextColor(Color.parseColor("#FFD54F"))
                            .show()
                    }

                    val l = File(this.getDir("saves", Context.MODE_PRIVATE), "$curText.save").readLines()
                    for (ll in l) {
                        Log.d("asd",ll)
                    }
                } else {
                    Snackbar.make(mDialogView,"Invalid save name",
                        Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.BLACK)
                        .setTextColor(Color.parseColor("#FFD54F"))
                        .show()
                }
            }
        }
        else{
            finish()
        }

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
        closefab()

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.edittext_dialog, null)
        val mAlertDialog = AlertDialog.Builder(this).setView(mDialogView).show()
        if (model.fileName == "") {
            mDialogView.dialogTextField.visibility = View.VISIBLE
            mDialogView.dialog_desc.text = "Create a new save file"
        } else {
            mDialogView.dialogTextField.visibility = View.GONE
            mDialogView.dialog_desc.text = "Overwrite save file ${model.fileName}?"
        }

        mAlertDialog.edit_dialog_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }

        mAlertDialog.edit_dialog_confirm.setOnClickListener {
            val thisView = it
            var curText = model.fileName
            if (curText == "") {
                curText = mAlertDialog.dialogEditText.text.toString()
            }
            if (curText != "") {
                try {
                    val f = File(this.getDir("saves", Context.MODE_PRIVATE), "$curText.save")
                    f.createNewFile()
                    f.printWriter().use {
                        it.println(model.facultyName)
                        it.println(model.majorName)
                        for (c in model.storedCards) {
                            var temp = ""
                            temp += c.text + "?"
                            temp += c.done.toString() + "?"
                            temp += c.checkedBoxes.joinToString(separator = ";") + "?"
                            temp += c.num.toString() + "?"
                            temp += c.progress.toString() + "?"
                            temp += c.items.joinToString(separator = ";") + "?"
                            temp += c.comment
                            it.println(temp)
                        }
                    }

                    // Current user
                    val currentUser = fAuth.currentUser

                    if(currentUser != null){
                        // Store the user data on Cloud if authenticated
                        val storageRef = storage.reference
                        val file = Uri.fromFile(f)
                        val userFileRef = storageRef.child("userData/${currentUser?.uid}/${file.lastPathSegment}")

                        var uploadTask = userFileRef.putFile(file)

                        uploadTask
                            .addOnFailureListener{
                                Snackbar.make(thisView,"Upload Fail",
                                    Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(Color.BLACK)
                                    .setTextColor(Color.parseColor("#FFD54F"))
                                    .show()
                            }
                            .addOnSuccessListener {
                                Snackbar.make(thisView,"Upload Success",
                                    Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(Color.BLACK)
                                    .setTextColor(Color.parseColor("#FFD54F"))
                                    .show()
                            }
                    }

                    mAlertDialog.dismiss()
                    model.changed = false
                    Snackbar.make(selectAll,"Save file created/updated successfully",
                        Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.BLACK)
                        .setTextColor(Color.parseColor("#FFD54F"))
                        .show()
                } catch(e : Exception){
                    Snackbar.make(mDialogView,"Unable to create/update save file",
                        Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(Color.BLACK)
                        .setTextColor(Color.parseColor("#FFD54F"))
                        .show()
                }

                val l = File(this.getDir("saves", Context.MODE_PRIVATE), "$curText.save").readLines()
                for (ll in l) {
                    Log.d("asd",ll)
                }
            } else {
                Snackbar.make(mDialogView,"Invalid save name",
                    Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.BLACK)
                    .setTextColor(Color.parseColor("#FFD54F"))
                    .show()
            }
        }
    }

    fun loadChecklist(s : String){
        model.fileName = s
        val lines = File(this.getDir("saves", Context.MODE_PRIVATE), "$s.save").readLines()
        model.storedCards.clear()
        model.cards.clear()
        Log.d("lel", lines.size.toString())
        model.facultyName = lines[0]
        model.majorName = lines[1]
        setlogo(lines[0])
        majorName.text = lines[1]
        for(i in 2 until lines.size -1) {
            val temp = lines[i].split("?").toList()
            val curCard = Card(temp[0], temp[1].toBoolean(),temp[3].toInt(), temp[5].split(";").toList())
            curCard.progress = temp[4].toInt()
            when(temp[2]) {
                "" -> curCard.checkedBoxes = mutableListOf()
                else -> curCard.checkedBoxes = temp[2].split(";").map{it.toInt() }.toMutableList()
            }
            curCard.comment = temp[6]
            model.storedCards.add(curCard)
        }
        model.cards.addAll(model.storedCards)
        newAdapter.notifyDataSetChanged()
    }

    fun deleteChecklist(v : View) {
        closefab()

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.delete_dialog, null)

        val mAlertDialog = AlertDialog.Builder(this).setView(mDialogView).show()

        mAlertDialog.delete_dialog_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }

        mAlertDialog.delete_dialog_confirm.setOnClickListener {
            val thisView = it

            if (model.fileName == "") {
                finish()
            } else {
                try {
                    val f = File(
                        this.getDir("saves", Context.MODE_PRIVATE),
                        "${model.fileName}.save"
                    )

                    // Current user
                    val currentUser = fAuth.currentUser

                    if(currentUser != null){
                        // Store the user data on Cloud if authenticated
                        val storageRef = storage.reference
                        val file = Uri.fromFile(f)
                        val userFileRef = storageRef.child("userData/${currentUser?.uid}/${file.lastPathSegment}")

                        // Delete file locally
                        f.delete()


                        var deleteTask = userFileRef.delete()

                        deleteTask
                            .addOnFailureListener{
                                Snackbar.make(thisView,"Delete Fail",
                                    Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(Color.BLACK)
                                    .setTextColor(Color.parseColor("#FFD54F"))
                                    .show()
                            }
                            .addOnSuccessListener {
                                Snackbar.make(thisView,"Delete Success",
                                    Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(Color.BLACK)
                                    .setTextColor(Color.parseColor("#FFD54F"))
                                    .show()
                            }
                    }
                    mAlertDialog.dismiss()
                    finish()
                } catch (e: Exception) {
                    Snackbar.make(
                        selectAll, "Error deleting save",
                        Snackbar.LENGTH_LONG
                    )
                        .setBackgroundTint(Color.BLACK)
                        .setTextColor(Color.parseColor("#FFD54F"))
                        .show()
                }
            }
        }
    }


    fun closefab() {
        fabExpand = false
        saveButton.animate().translationY(-200f)
        deleteButton.animate().translationY(-400f)
    }

    fun openfab() {
        fabExpand = true
        saveButton.animate().translationY(0f)
        deleteButton.animate().translationY(0f)
    }



}
