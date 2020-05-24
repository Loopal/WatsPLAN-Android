package com.wwjz.watsplan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val fAuth = FirebaseAuth.getInstance()

        if(fAuth.currentUser != null){
            val intent = Intent()
            intent.setClass(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        registerButton.setOnClickListener {
            val name = registerName.text.toString().trim()
            val email = registerEmail.text.toString().trim()
            val password = registerPassword.text.toString().trim()
            val passwordConfirmed = registerConfirmPassword.text.toString().trim()

            if(TextUtils.isEmpty(name)){
                registerName.error = "Name is Required"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(email)){
                registerEmail.error = "Email is Required"
                return@setOnClickListener
            }

            if(password.length < 6){
                registerPassword.error = "Password Must be at Least 6 Characters"
                return@setOnClickListener
            }

            if(!password.equals(passwordConfirmed)){
                registerConfirmPassword.error = "Password Should be Same"
                return@setOnClickListener
            }

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    val user = fAuth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener {
                            if(it.isSuccessful){
                                Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show()
                            }
                        }
                    val intent = Intent()
                    intent.setClass(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this, "Error " + (it.exception?.message ?:-1), Toast.LENGTH_SHORT).show()
                }
            }

        }

        registerSwitch.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}
