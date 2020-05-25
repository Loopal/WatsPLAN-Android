package com.wwjz.watsplan

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    val fAuth = FirebaseAuth.getInstance()
    var handler = Handler()

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

            fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        Snackbar.make(loginButton,"Login Successfully",
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
                        Snackbar.make(loginButton,"Error " + (it.exception?.message ?:-1),
                            Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.BLACK)
                            .setTextColor(Color.parseColor("#FFD54F"))
                            .show()
                    }
                }
                // Catch the failures
                .addOnFailureListener {
                    Snackbar.make(loginButton,it.localizedMessage,
                        Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.BLACK)
                        .setTextColor(Color.parseColor("#FFD54F"))
                        .show()
                }
        }

        loginSwitch.setOnClickListener {
            handler.postDelayed({
                val intent = Intent()
                intent.setClass(this, RegisterActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }, 100)
        }
    }
}
