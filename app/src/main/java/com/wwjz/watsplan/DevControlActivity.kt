package com.wwjz.watsplan

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_dev_control.*
import kotlinx.android.synthetic.main.activity_dev_control.createButton
import kotlinx.android.synthetic.main.activity_main.*

class DevControlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_control)

        updateButton.setOnClickListener {

            val intent = Intent()
            intent.setClass(this, DepthControlActivity::class.java)
            startActivity(intent)

            if (createButton.currentTextColor == Color.BLACK) {
                createButton.setBackgroundColor(Color.BLACK)
                createButton.setTextColor(resources.getColor(R.color.uwYellow))
            } else {
                createButton.setBackgroundColor(resources.getColor(R.color.uwYellow))
                createButton.setTextColor(Color.BLACK)

            }

        }

        createButton.setOnClickListener{

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

        }
    }
}
