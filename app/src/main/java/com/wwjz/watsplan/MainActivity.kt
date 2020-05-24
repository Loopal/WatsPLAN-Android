package com.wwjz.watsplan

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
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
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.android.synthetic.main.activity_register.*


var permissionDeny = true

class MainActivity : AppCompatActivity() {
    val faculties = mutableListOf<String>()
    val programs = mutableListOf<String>()
    val saves = listOf<String>()
    var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                    val fAuth = FirebaseAuth.getInstance()
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
                        }, 1000)
                    }
                    else{
                        handler.postDelayed({
                            val intent = Intent()
                            intent.setClass(this, LoginActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                            finish()
                        }, 1000)
                    }
                    true
                }
                R.id.nav_home -> {
                    true
                }
                R.id.nav_dev -> {

                    if(permissionDeny){
                        MaterialDialog(this).show{
                            title(text = "Permission")
                            message(text = "You have no permission to access this section")
                            negativeButton(text = "Back")
                        }

                    }
                    else{
                        handler.postDelayed({
                            val intent = Intent()
                            intent.setClass(this, DevControlActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                            finish()
                        }, 1000)
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
                intent.putExtra("Major", program_dropdown.text.toString())
                intent.setClass(this, ChecklistActivity::class.java)
                startActivity(intent)
            } else {
                Snackbar.make(createSubmit,"Invalid faculty/program, please try again.",
                    Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.BLACK)
                    .setTextColor(Color.parseColor("#FFD54F"))
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
    }

}