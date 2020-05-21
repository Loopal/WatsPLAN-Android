package com.wwjz.watsplan

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val majors = arrayOf("Item 1", "Item 2", "Item 3", "Item 4")

        val adapter = ArrayAdapter<String>(
            this,
            R.layout.dropdown_menu_popup_item,
            majors
        )

        filled_exposed_dropdown.setAdapter(adapter)

        createButton.setOnClickListener{
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