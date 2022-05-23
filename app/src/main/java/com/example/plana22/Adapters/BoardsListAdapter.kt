package com.example.plana22.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plana22.Models.Board
import com.example.plana22.R
import kotlinx.android.synthetic.main.item_board.view.*

class BoardsListAdapter(val context: Context,
                        val list: ArrayList<Board>)
    : RecyclerView.Adapter<BoardsListAdapter.MyViewHolder>(){

    private var onClickListener : OnClickListener? = null
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_board, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]

        if(holder is MyViewHolder){

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.itemView.rv_board_list_image)

            holder.itemView.tv_name.text = model.name
            holder.itemView.tv_created_by.text = ("Created by: " + model.createdBy)

            holder.itemView.setOnClickListener {
                onClickListener?.onClick(position, model)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    /**A function for OnClickListener where the Interface is the expected parameter..*/
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**An interface for onclick items*/
    interface OnClickListener{
        fun onClick(position: Int,model : Board)
    }


}