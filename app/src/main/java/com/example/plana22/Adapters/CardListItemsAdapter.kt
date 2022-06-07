package com.example.plana22.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.plana22.Activities.operations.CardDetailsActivity
import com.example.plana22.Activities.operations.TasksListActivity
import com.example.plana22.Models.Card
import com.example.plana22.R
import kotlinx.android.synthetic.main.item_card.view.*

class CardListItemsAdapter(val context: Context, val list: ArrayList<Card>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener : OnClickListener? = null

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CardViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_card, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is CardViewHolder){
            if (model.labelColor.isNotEmpty()){
                holder.itemView.view_label_color.visibility = View.VISIBLE
                holder.itemView.view_label_color.setBackgroundColor(Color.parseColor(model.labelColor))
            }else{
                holder.itemView.view_label_color.visibility = View.GONE
            }

            holder.itemView.tv_card_name.text = model.name
            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick(position)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(cardPosition: Int)
    }
}