package com.example.plana22.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.plana22.DetailFragment
import com.example.plana22.R
import com.example.plana22.RoomDetail.DetailDao
import com.example.plana22.RoomDetail.TaskList
import kotlinx.android.synthetic.main.item_rv_detail.view.*

class DetailAdapter(
    val context: Context,
    val detailDao : DetailDao
): RecyclerView.Adapter<DetailAdapter.DetailViewHolder>() {

    var items  = mutableListOf<TaskList>()

    var detailFragment = DetailFragment()

    class DetailViewHolder(view: View ): RecyclerView.ViewHolder(view) {
        var ivDelete = view.ivDelete
    }

    fun setListData(data: ArrayList<TaskList>) {
        this.items = data

        //This notifies  the UI that a change in the tasklists has occurred and updates it immediately
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_rv_detail, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val model = items[position]

        holder.itemView.detailTaskID.text = getItemId(position).toString()
        holder.itemView.tvTaskDetail.text =  model.tasks
        holder.ivDelete.setOnClickListener {

            //if(context is OverviewFragment){

            detailFragment.deleteRecordDialog(position, detailDao)

                //notifyItemRemoved(position)
                //notifyItemChanged(position)
                //notifyDataSetChanged()
            //}
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }
}


