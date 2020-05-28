package com.wwjz.watsplan

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Integer.min


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

    override fun onBindViewHolder(holder: cardViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            for (payload in payloads) {
                if (payload is String) {
                    holder.cardProgress!!.progress = model.cards[position].progress
                }
            }
        }
    }


    override fun onBindViewHolder(holder: cardViewHolder, position: Int) {
        if (model.cards[position].items.size > 3) {
            if (model.cards[position].num == model.cards[position].items.size) {
                holder.cardText!!.text = "Select All From " + model.cards[position].text
            } else {
                holder.cardText!!.text = "Select " + model.cards[position].text + "From"
            }
        } else {
            holder.cardText!!.text = "Select " + model.cards[position].text
        }
        val newAdapter = CheckBoxAdapter(position,cxt, this)
        val maxLength = model.cards[position].items.maxBy {it.length}?.length
        var colNum = min(model.cards[position].items.size, 3)
        if (maxLength != null) {
            if(maxLength >= 40){
                colNum = 1;
            }
        }
        val gridLayoutManager = GridLayoutManager(cxt, colNum)
        holder.cardGrid!!.layoutManager = gridLayoutManager
        holder.cardGrid!!.adapter = newAdapter

        if (model.cards[position].num == 1) {
            holder.cardComment!!.visibility = View.GONE
        } else {
            holder.cardComment!!.visibility = View.VISIBLE
        }

        holder.cardProgress!!.progress = model.cards[position].progress

    }

    fun applyFilter(low: Int, high : Int) {
        model.cards.clear()

        for (c in model.storedCards) {
            if (c.progress in low..high) {
                model.cards.add(c)
            }
        }
        Handler().post(Runnable { notifyDataSetChanged() })
    }

}

class cardViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    var cardText: TextView? = null
    var cardGrid: RecyclerView? = null
    var cardComment: EditText? = null
    var cardProgress: ProgressBar? = null

    init {
        cardText = v.findViewById(R.id.cardDescription)
        cardGrid = v.findViewById(R.id.boxGrid)
        cardComment = v.findViewById(R.id.cardComment)
        cardProgress = v.findViewById(R.id.cardProgress)
    }

}

class CheckBoxAdapter(pos:Int, context:Context, a: cardRecyclerAdapter) : RecyclerView.Adapter<gridViewHolder>() {
    var context : Context = context
    val p = pos
    var model = Model.mInstance
    var parentAdapter : cardRecyclerAdapter = a
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): gridViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.grid_item, parent, false);
        return gridViewHolder(v)
    }

    override fun getItemCount(): Int {
        return model.cards[p].items.size
    }

    override fun onBindViewHolder(holder: gridViewHolder, position: Int) {
        holder.cbox!!.text = model.cards[p].items[position]
        holder.cbox!!.isChecked = model.cards[p].checkedBoxes.contains(position)

        holder.cbox!!.setOnClickListener {
            var curBox = it as CheckBox
            if (curBox.isChecked) {
                model.cards[p].checkedBoxes.add(position)
                if (model.cards[p].checkedBoxes.size > model.cards[p].num) {
                    var uncheckPos = model.cards[p].checkedBoxes.removeAt(0)
                    Handler().post(Runnable { notifyItemChanged(uncheckPos) })
                }
                model.cards[p].progress = when (model.cards[p].checkedBoxes.size == model.cards[p].num) {
                    true -> 100
                    false -> model.cards[p].progress + 100 / model.cards[p].num
                }
                Handler().post(Runnable { parentAdapter.notifyItemChanged(p, "") })

            } else {
                model.cards[p].checkedBoxes.remove(position)
                model.cards[p].progress = when (model.cards[p].checkedBoxes.size == 0) {
                    true -> 0
                    false -> model.cards[p].progress - 100 / model.cards[p].num
                }
                Handler().post(Runnable { notifyItemChanged(position) })
                Handler().post(Runnable { parentAdapter.notifyItemChanged(p,"") })

            }
            Log.d("box",model.cards[p].checkedBoxes.size.toString())
        }
    }

}

class gridViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    var cbox: CheckBox? = null

    init {
        cbox = v.findViewById(R.id.cb_item)
    }
}
