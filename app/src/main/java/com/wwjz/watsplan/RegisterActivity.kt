package com.wwjz.watsplan

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    var handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val fAuth = FirebaseAuth.getInstance()

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
                finish()
            }, 100)
        }

    }
}
