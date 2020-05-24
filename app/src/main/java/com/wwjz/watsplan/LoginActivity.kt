package com.wwjz.watsplan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    val fAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener {
            val email = loginEmail.text.toString().trim()
            val password = loginPassword.text.toString().trim()

            if(TextUtils.isEmpty(email)){
                loginEmail.error = "Email is Required"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(password)){
                loginPassword.error = "Password is Required"
                return@setOnClickListener
            }

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    intent.setClass(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this, "Error " + (it.exception?.message ?:-1), Toast.LENGTH_SHORT).show()
                }
            }
        }

        loginSwitch.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
