package com.example.plana22.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plana22.Models.User
import com.example.plana22.R
import com.example.plana22.utils.Constants
import kotlinx.android.synthetic.main.item_member.view.*

class MembersListItemsAdapter(val context : Context,
                              val list : ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var onclickListener : OnclickListener? = null

    class MembersViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MembersViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_member,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MembersViewHolder){

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.iv_member_image)

            holder.itemView.tv_member_name.text = "${model.firstName }  ${model.lastName}"
            //holder.itemView.tv_member_name.text = "${model.firstName}"

            holder.itemView.tv_member_email.text = model.email


            //Shows the Check image if the item is selected and removes it if it isn't
            if (model.selected){
                holder.itemView.iv_selected_member.visibility = View.VISIBLE
            }else{
                holder.itemView.iv_selected_member.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                if (onclickListener != null) {
                    if (model.selected) {
                        onclickListener!!.onClick(position, model, Constants.UNSELECT)
                    } else {
                        onclickListener!!.onClick(position, model, Constants.SELECT)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun setOnClickListener(onclickListener: OnclickListener){
        this.onclickListener = onclickListener
    }

    interface OnclickListener{
        fun onClick(position: Int, user: User, action: String)
    }
}

