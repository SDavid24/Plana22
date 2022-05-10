package com.example.plana22.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.plana22.fragments.OverviewFragment
import com.example.plana22.R
import com.example.plana22.RoomDetail.DetailDao
import com.example.plana22.RoomDetail.DetailEntity
import kotlinx.android.synthetic.main.item_rv_overview.view.*

class OverviewAdapter(
    val context: Context,
    val list: ArrayList<DetailEntity>,
    val detailDao: DetailDao

) : RecyclerView.Adapter <OverviewAdapter.OverviewViewHolder>(){
    private var onClickListener: OnClickListener? = null

    var overviewFragment = OverviewFragment()
     inner class OverviewViewHolder(view: View): RecyclerView.ViewHolder(view) {

         /**Method that configures the popup icon that's embedded in every recyclerview*/
        fun popupMenus(view: View?, detailDao : DetailDao, position: Int) {
             val popupMenus = PopupMenu(context, view)
            popupMenus.inflate(R.menu.options_menu)
            popupMenus.setOnMenuItemClickListener {

                when (it.itemId) {
                    //What happens when Delete is clicked on
                    R.id.delete -> {
                         overviewFragment.deleteRecordDialog(position, detailDao) //Calling the deleteRecordDialog from the OverviewActivity
                        Toast.makeText(
                            context, "This is a $position item", Toast.LENGTH_SHORT
                        ).show()

                    }
                    R.id.item_1 -> {
                        Toast.makeText(context,
                            "item 1 clicked ", Toast.LENGTH_SHORT).show()
                        //true
                    }
                    R.id.item_2 -> {
                        Toast.makeText(
                            context,
                            "item 2 clicked", Toast.LENGTH_SHORT).show()
                        //true
                    }
                    R.id.item_3 -> {
                        Toast.makeText(context,
                            "item 3 clicked", Toast.LENGTH_SHORT).show()
                        //true
                    }
                }
                 false
            }

            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu,true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_rv_overview, parent, false)
        return OverviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: OverviewViewHolder, position: Int) {

        val model = list[position]

        holder.itemView.ovTaskID.text = model.id.toString()
        holder.itemView.rv_overview_image.setImageResource(model.image!!).toString()
        holder.itemView.tvCategory.text = model.category
        holder.itemView.ovTaskNumber.text = model.taskList.size.toString()

        when {
            holder.itemView.ovTaskNumber.text.toString().toInt() == 0 -> {
                holder.itemView.ovTaskText.text = "task"
            }
            holder.itemView.ovTaskNumber.text.toString().toInt() == 1 -> {
                holder.itemView.ovTaskText.text = "task"
            }
            else -> {
                holder.itemView.ovTaskText.text = "tasks"
            }
        }

        //OnclickListener that executes what should happen a popup icon is clicked \
        // on and it content too
        holder.itemView.textViewOptions.setOnClickListener {
            OverviewViewHolder(it).popupMenus(it, detailDao, model.id)
        }

        //Finally, add an onclickListener to the item to make it go to the Detail Activity
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, model)
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    /** A function to bind the onclickListener.*/
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**Create an interface for onclickListener*/
    interface OnClickListener{
        fun onClick(position: Int, model: DetailEntity)
    }
}

