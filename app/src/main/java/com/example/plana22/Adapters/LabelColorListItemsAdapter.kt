package com.example.plana22.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.plana22.R
import kotlinx.android.synthetic.main.item_label_color.view.*

class LabelColorListItemsAdapter(private val context: Context,
                                 private val list: ArrayList<String>,
                                 private val mSelectedColor : String)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onclickListener : OnclickListener? = null


    class LabelColorViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return LabelColorViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_label_color, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        if (holder is LabelColorViewHolder){
            holder.itemView.view_main.setBackgroundColor(Color.parseColor(item))

            if (item == mSelectedColor){
                holder.itemView.iv_selected_color.visibility = View.VISIBLE
            }else{
                holder.itemView.iv_selected_color.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                if (onclickListener != null){
                    onclickListener!!.onClick(position, item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnclickListener{
        fun onClick(position: Int, color: String)
    }
}