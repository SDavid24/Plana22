package com.example.plana22.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plana22.Activities.operations.TasksListActivity
import com.example.plana22.Models.Card
import com.example.plana22.R
import kotlinx.android.synthetic.main.item_card.view.*
import kotlinx.android.synthetic.main.item_card_selected_member.view.*
import models.SelectedMembers

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

            if  ((context as TasksListActivity).mAssignedMembersDetailsList.size > 0){
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

                for(i in context.mAssignedMembersDetailsList.indices){
                    for (j in model.assignedTo){
                        if (context.mAssignedMembersDetailsList[i].id == j){
                            val selectedMembers = SelectedMembers(
                                context.mAssignedMembersDetailsList[i].id,
                                context.mAssignedMembersDetailsList[i].image
                            )
                            selectedMembersList.add(selectedMembers)
                        }
                    }
                }
                if (selectedMembersList.size > 0){
                    if (selectedMembersList.size == 1 && selectedMembersList[0].id
                        == model.createdBy) {
                        holder.itemView.rv_card_selected_members_list.visibility = View.GONE

                    }else{
                        holder.itemView.rv_card_selected_members_list.visibility = View.VISIBLE

                        holder.itemView.rv_card_selected_members_list.layoutManager =
                            GridLayoutManager(context, 4)
                        val adapter = CardMemberListItemAdapter(context,
                            selectedMembersList, false)

                        holder.itemView.rv_card_selected_members_list.adapter = adapter
                        adapter.setOnClickListener(object : CardMemberListItemAdapter.OnClickListener{
                            override fun onClick() {
                                if (onClickListener != null){
                                    onClickListener!!.onClick(position)
                                }
                            }

                        })
                    }
                }else{
                    holder.itemView.rv_card_selected_members_list.visibility = View.GONE
                }
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