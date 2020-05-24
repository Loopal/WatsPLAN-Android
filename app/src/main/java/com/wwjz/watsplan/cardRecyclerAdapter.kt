package com.wwjz.watsplan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class cardRecyclerAdapter(context : Context) : RecyclerView.Adapter<cardViewHolder>() {
    var cxt = context
    var model = Model.mInstance


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cardViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.checklist_item, parent, false);
        return cardViewHolder(v)
    }

    override fun getItemCount(): Int {
        return model.cards.size
    }

    override fun onBindViewHolder(holder: cardViewHolder, position: Int) {
        holder.cardText!!.text = model.cards[position].text
        val newAdapter = CheckBoxAdapter(position,cxt)
        val gridLayoutManager = GridLayoutManager(cxt, 3)
        holder.cardGrid!!.layoutManager = gridLayoutManager
        holder.cardGrid!!.adapter = newAdapter
    }

}

class cardViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    var cardText: TextView? = null
    var cardGrid: RecyclerView? = null

    init {
        cardText = v.findViewById(R.id.cardDescription)
        cardGrid = v.findViewById(R.id.boxGrid)
    }

}

class CheckBoxAdapter(pos:Int, context:Context) : RecyclerView.Adapter<gridViewHolder>() {
    var context : Context = context
    val p = pos
    var model = Model.mInstance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): gridViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.grid_item, parent, false);
        return gridViewHolder(v)
    }

    override fun getItemCount(): Int {
        return model.cards[p].items.size
    }

    override fun onBindViewHolder(holder: gridViewHolder, position: Int) {
        holder.cbox!!.text = model.cards[p].items[position]
    }


}

class gridViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    var cbox: CheckBox? = null

    init {
        cbox = v.findViewById(R.id.cb_item)
    }
}
