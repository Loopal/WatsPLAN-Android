package com.wwjz.watsplan

import android.widget.CheckBox

class Card(s: String, b:Boolean, n : Int, i: List<String>) {
    var text = ""
    var done: Boolean = false
    var checkedBoxes : MutableList<Int> = mutableListOf<Int>()
    var num = 0
    var progress = 0
    var items: List<String> = listOf<String>()

    init {
        text = s
        done = b
        num = n
        items = i
    }

}
