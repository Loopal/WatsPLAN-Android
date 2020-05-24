package com.wwjz.watsplan

import android.widget.CheckBox

class Card(s: String, b:Boolean, i: MutableList<String>) {
    var text = ""
    var checked: Boolean = false
    var items: MutableList<String> = mutableListOf<String>()

    init {
        text = s
        checked = b
        items = i
    }


}
