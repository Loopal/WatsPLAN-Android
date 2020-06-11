package com.wwjz.watsplan

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import java.io.File

class RegisterActivity : AppCompatActivity() {

    var handler = Handler()

    // Create the Cloud Storage for user data
    val storage = FirebaseStorage.getInstance()
    // User Auth
    val fAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if(fAuth.currentUser != null){
            handler.postDelayed({
                val intent = Intent()
                intent.setClass(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }, 100)
        }

        registerButton.setOnClickListener {
            val thisView = it
            val name = registerName.text.toString().trim()
            val email = registerEmail.text.toString().trim()
            val password = registerPassword.text.toString().trim()
            val passwordConfirmed = registerConfirmPassword.text.toString().trim()
            // Regex for password strength check
            val regexPasswordChecker = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")

            // Checker for each textfield
            if(TextUtils.isEmpty(name)){
                registerName.error = "Name is Required"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(email)){
                registerEmail.error = "Email is Required"
                return@setOnClickListener
            }

            if(!password.matches(regexPasswordChecker)){
                registerPassword.error = "Password should be Minimum Eight Characters, at Least One Letter, One Number and One Special Character"
                return@setOnClickListener
            }

            if(!password.equals(passwordConfirmed)){
                registerConfirmPassword.error = "Password should be Same"
                return@setOnClickListener
            }

            // Create an account with email and password
            fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val user = fAuth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener {
                                if(it.isSuccessful){
                                    Snackbar.make(registerButton,"New Account Created Successfully",
                                        Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(Color.BLACK)
                                        .setTextColor(Color.parseColor("#FFD54F"))
                                        .show()
                                    /*val intent = Intent()
                                    intent.setClass(this, MainActivity::class.java)
                                    startActivity(intent)*/
                                    handler.postDelayed({
                                        val intent = Intent()
                                        intent.setClass(this, MainActivity::class.java)
                                        startActivity(intent)
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                                        finish()
                                    }, 100)
                                }
                            }

                        // Store the user data on Cloud if authenticated
                        val storageRef = storage.reference
                        val fileList = File(this.getDir("saves", Context.MODE_PRIVATE).toURI()).walkTopDown().forEach {
                            if(it.name != "app_saves"){
                                val file = Uri.fromFile(it)
                                val userFileRef = storageRef.child("userData/${user?.uid}/${file.lastPathSegment}")

                                var uploadTask = userFileRef.putFile(file)

                                uploadTask
                                    .addOnFailureListener{
                                        Snackbar.make(thisView, "Cloud Sync Fail",
                                            Snackbar.LENGTH_LONG)
                                            .setBackgroundTint(Color.BLACK)
                                            .setTextColor(Color.parseColor("#FFD54F"))
                                            .show()
                                    }
                                    .addOnSuccessListener {
                                        Snackbar.make(thisView,"Cloud Sync Succeed",
                                            Snackbar.LENGTH_LONG)
                                            .setBackgroundTint(Color.BLACK)
                                            .setTextColor(Color.parseColor("#FFD54F"))
                                            .show()
                                    }
                            }
                        }

                    }
                    else{
                        Snackbar.make(registerButton,"Error " + (it.exception?.message ?:-1),
                            Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.BLACK)
                            .setTextColor(Color.parseColor("#FFD54F"))
                            .show()
                    }
                }
                // Catch the failures
                .addOnFailureListener {
                    Snackbar.make(registerButton,it.localizedMessage,
                        Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.BLACK)
                        .setTextColor(Color.parseColor("#FFD54F"))
                        .show()
                }

        }

        registerSwitch.setOnClickListener {
            handler.postDelayed({
                val intent = Intent()
                intent.setClass(this, LoginActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }, 100)
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
}
