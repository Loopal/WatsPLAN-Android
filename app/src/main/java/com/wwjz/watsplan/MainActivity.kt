package com.wwjz.watsplan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.android.synthetic.main.activity_register.*
import java.io.File

var permissionDeny = true

class MainActivity : AppCompatActivity() {
    val faculties = mutableListOf<String>()
    val programs = mutableListOf<String>()
    val options = mutableListOf<String>()
    val saves = mutableListOf<String>()
    var handler = Handler()
    // Create the Cloud Storage for user data
    val storage = FirebaseStorage.getInstance()
    // User Auth
    val fAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Current user
        val currentUser = fAuth.currentUser

        if(currentUser != null){
            // Load the user data from cloud
            val storageRef = storage.getReference("userData/${currentUser.uid}")
            val fileRefList = storageRef.listAll().addOnSuccessListener {
                it.items.forEach {
                    val ONE_MEGABYTE: Long = 1024 * 1024
                    /*it.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                        val f = File(this.getDir("saves", Context.MODE_PRIVATE), "$it.save")
                    }.addOnFailureListener {
                        // Handle any errors
                    }
                    */
                    val localFile = File(this.getDir("saves", Context.MODE_PRIVATE), "${it.name}")
                    it.getFile(localFile)
                        .addOnFailureListener{
                            Snackbar.make(loadSubmit,"Download Fail",
                                Snackbar.LENGTH_LONG)
                                .setBackgroundTint(Color.BLACK)
                                .setTextColor(Color.parseColor("#FFD54F"))
                                .show()
                        }
                        .addOnSuccessListener{
                            Snackbar.make(loadSubmit,"Download Success",
                                Snackbar.LENGTH_LONG)
                                .setBackgroundTint(Color.BLACK)
                                .setTextColor(Color.parseColor("#FFD54F"))
                                .show()
                        }
                }
            }
        }


        //Get local saves
        val l = this.getDir("saves", Context.MODE_PRIVATE)
            .walk()
            .filter { it.extension == "save" }
            .forEach { saves.add(it.nameWithoutExtension) }

        val sadapter = ArrayAdapter<String>(
            this,
            R.layout.dropdown_menu_popup_item,
            saves
        )

        save_dropdown.setAdapter(sadapter)

        //Get DB
        val db = FirebaseFirestore.getInstance()
        //Query for faculties
        val fdoc = db.collection("/Faculties/")


        fdoc.get()
            .addOnSuccessListener { documents ->
                faculties.clear()
                for (document in documents) {
                    faculties.add(document.id)
                    Log.d("qq", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("qq", "Error getting documents: ", exception)
            }

        val fadapter = ArrayAdapter<String>(
            this,
            R.layout.dropdown_menu_popup_item,
            faculties
        )

        faculty_dropdown.setAdapter(fadapter)

        faculty_dropdown.setOnItemClickListener { parent, view, position, id ->
            //Query for programs
            program_dropdown.completionHint
            program_dropdown.setText("")
            val pdoc = db.collection(parent.adapter.getItem(position).toString())

            pdoc.get()
                .addOnSuccessListener { documents ->
                    programs.clear()
                    for (document in documents) {
                        programs.add(document.id)
                        Log.d("qq", "${document.id} => ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("qq", "Error getting documents: ", exception)
                }

            val padapter = ArrayAdapter<String>(
                this,
                R.layout.dropdown_menu_popup_item,
                programs
            )
            program_dropdown.setAdapter(padapter)
        }

        program_dropdown.setOnItemClickListener { parent, view, position, id ->
            //Query for option (if applicable)
            option_dropdown.completionHint
            option_dropdown.setText("")
            val odoc = db.collection(parent.adapter.getItem(position).toString())

            odoc.get()
                .addOnSuccessListener { documents ->
                    options.clear()
                    for (document in documents) {
                        options.add(document.id)
                        Log.d("qq", "${document.id} => ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("qq", "Error getting documents: ", exception)
                }

            val oadapter = ArrayAdapter<String>(
                this,
                R.layout.dropdown_menu_popup_item,
                options
            )
            option_dropdown.setAdapter(oadapter)
        }
        /*
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
        }*/

        /*createButton.setOnClickListener{

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

        }*/

        //navigation.inflateMenu(R.menu.drawer_menu)

        navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_login ->{
                    /*
                    // Choose authentication providers
                    val providers = arrayListOf(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build())

                    // Create and launch sign-in intent
                    startActivityForResult(
                        AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setLogo(R.drawable.logo)
                            .setTheme(R.style.AppTheme_NoActionBar)
                            .build(),
                        1)
                    true*/

                    if(fAuth.currentUser != null){
                        Snackbar.make(createSubmit,"Current Login with " + fAuth.currentUser!!.displayName.toString(),
                            Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.BLACK)
                            .setTextColor(Color.parseColor("#FFD54F"))
                            .show()
                        handler.postDelayed({
                            val intent = Intent()
                            intent.setClass(this, MainActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                            finish()
                        }, 100)
                    }
                    else{
                        handler.postDelayed({
                            val intent = Intent()
                            intent.setClass(this, LoginActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        }, 100)
                    }
                    true
                }
                R.id.nav_home -> {
                    true
                }
                R.id.nav_dev -> {
                    val user = FirebaseAuth.getInstance().currentUser
                    /*if(permissionDeny){
                        MaterialDialog(this).show{
                            title(text = "Permission")
                            message(text = "You have no permission to access this section")
                            negativeButton(text = "Back")
                        }

                    }*/
                    if (user != null) {
                        if(user.email == "jzdevelopments@gmail.com" || user.email == "vanjor1014@gmail.com"){
                            handler.postDelayed({
                                val intent = Intent()
                                intent.setClass(this, DevControlActivity::class.java)
                                startActivity(intent)
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                            }, 100)
                        }
                        else{
                            Snackbar.make(navigation,"You have no permission to access this section",
                                Snackbar.LENGTH_LONG)
                                .setBackgroundTint(Color.BLACK)
                                .setTextColor(Color.parseColor("#FFD54F"))
                                .show()
                        }
                    }
                    else{
                        Snackbar.make(navigation,"Please login to access this section",
                            Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.BLACK)
                            .setTextColor(Color.parseColor("#FFD54F"))
                            .show()
                    }
                    true
                }
                else -> {
                    FirebaseAuth.getInstance().signOut()
                    Snackbar.make(createSubmit,"Logout Successfully",
                        Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.BLACK)
                        .setTextColor(Color.parseColor("#FFD54F"))
                        .show()
                    true
                }
            }
        }


        loadSubmit.setOnClickListener {
            if (saves.contains(save_dropdown.text.toString())) {
                val intent = Intent()
                intent.putExtra("Save", save_dropdown.text.toString())
                intent.setClass(this, ChecklistActivity::class.java)
                startActivity(intent)
            } else {
                Snackbar.make(createSubmit,"Invalid save, please try again.",
                    Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.BLACK)
                    .setTextColor(Color.parseColor("#FFD54F"))
                    .show()
            }
        }

        createSubmit.setOnClickListener {
            if (programs.contains(program_dropdown.text.toString())) {
                val intent = Intent()
                intent.putExtra("Faculty", faculty_dropdown.text.toString())
                intent.putExtra("Major", program_dropdown.text.toString())
                intent.setClass(this, ChecklistActivity::class.java)
                if(options.contains(option_dropdown.text.toString())){
                    if(option_dropdown.text.toString() != "Just click CREATE button if no option"){
                        intent.putExtra("Option", option_dropdown.text.toString())
                        startActivity(intent)
                    }
                    else
                        startActivity(intent)
                }
                else if(option_dropdown.text.toString() == "")
                    startActivity(intent)
                else{
                    Snackbar.make(createSubmit,"Invalid faculty/program/option, please try again.",
                        Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.BLACK)
                        .setTextColor(Color.parseColor("#FFD54F"))
                        .show()
                }
            } else {
                Snackbar.make(createSubmit,"Invalid faculty/program, please try again.",
                    Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.BLACK)
                    .setTextColor(Color.parseColor("#FFD54F"))
                    .show()
            }
        }
    }

    // Hide the softKeyboard when change focus
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(currentFocus != null){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1){
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                user?.let{
                    val email = user.email
                    if(email == "jzdevelopments@gmail.com" || email == "vanjor1014@gmail.com")
                        permissionDeny = false
                }
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }*/

    fun onNavButtonClick(v: View) {
        drawer_layout.openDrawer(Gravity.LEFT)
    }

}