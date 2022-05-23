package com.example.plana22.Adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plana22.Activities.operations.TasksListActivity
import com.example.plana22.Models.Task
import com.example.plana22.R
import kotlinx.android.synthetic.main.item_task.view.*

class TaskListItemsAdapter(val context: Context,
                           val list: ArrayList<Task>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)


        // Here the layout params are converted dynamically according to the screen size as width is 70% and height is wrap_content.
        val layoutParams = LinearLayout.LayoutParams((
                parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)

        // Here the dynamic margins are applied to the view.
        layoutParams.setMargins((15.toDp().toPx()), 0,  (40.toDp().toPx()), 0)
        view.layoutParams = layoutParams

        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if(holder is TaskViewHolder){

            //Setting the last position of the recycler view to be the Add Task textView button
            if (position == list.size - 1) {
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.ll_task_item.visibility = View.GONE
            } else {
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.ll_task_item.visibility = View.VISIBLE
            }

            //Setting the task title to also be the title in the database
            holder.itemView.tv_task_list_title.text = model.title

            //setting the click listener for the Add task tv Button. making it invisible and  it disappear and the text edit space for adding a task visible
            holder.itemView.tv_add_task_list.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.cv_add_task_list_name.visibility = View.VISIBLE
            }

            //Doing the reverse above when you click on the cancel icon
            holder.itemView.ib_close_list_name.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.cv_add_task_list_name.visibility = View.GONE
            }

            //Setting the click listener for the Save icon. this creates the task
/*
            holder.itemView.ib_done_list_name.setOnClickListener {
                val listName = holder.itemView.et_task_list_name.text.toString()

                //Checks if the edit text space is null before trying to save it
                if (listName.isNotEmpty()) {
                    if (context is TasksListActivity) {
                        context.createTaskList(listName)
                        holder.itemView.ll_title_view.visibility = View.VISIBLE
                        holder.itemView.cv_edit_task_list_name.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(
                        context, "Please enter List name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
*/

            holder.itemView.ib_done_list_name.setOnClickListener {
                val listName = holder.itemView.et_task_list_name.text.toString()

                if (listName.isNotEmpty()) {
                    if (context is TasksListActivity) {
                        context.createTaskList(listName)
                        Toast.makeText(
                            context, "Task successfully created.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context, "Please enter List name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


            //click listener for the edit icon.
            //sets the text in the edit title text space to the already saved title
            //Makes the title text title layout invisible
            //then makes the edit title text visible
            holder.itemView.ib_edit_list_name.setOnClickListener {
                holder.itemView.et_edit_task_list_name.setText(model.title)
                holder.itemView.ll_title_view.visibility = View.GONE
                holder.itemView.cv_edit_task_list_name.visibility = View.VISIBLE
            }

            //Closes the edit title text space
            holder.itemView.ib_close_editable_view.setOnClickListener {
                holder.itemView.ll_title_view.visibility = View.VISIBLE
                holder.itemView.cv_edit_task_list_name.visibility = View.GONE
            }

            //Setting the click listener for the Update icon. this updates the task title
            holder.itemView.ib_done_edit_list_name.setOnClickListener {
                val listName = holder.itemView.et_edit_task_list_name.text.toString()

                //Checks if the edit text title space is null before trying to update it
                if (listName.isNotEmpty()) {
                    if (context is TasksListActivity) {
                        context.updateTaskList(position, listName, model)
                    }
                } else {
                    Toast.makeText(
                        context, "Please enter List name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            //Click listener that deletes the task
            holder.itemView.ib_delete_list.setOnClickListener {
                alertDialogForDeleteList(position, model.title)
            }

            //Click listener for the ADD CARD textView button that adds task card to the title
            //Makes the Add Card textView button  invisible
            //then makes the card name edit text visible
            holder.itemView.tv_add_card.setOnClickListener {
                holder.itemView.tv_add_card.visibility = View.GONE
                holder.itemView.cv_add_card.visibility = View.VISIBLE
            }

            //Closes the edit Add card text space
            holder.itemView.ib_close_card_name.setOnClickListener {
                holder.itemView.tv_add_card.visibility = View.VISIBLE
                holder.itemView.cv_add_card.visibility = View.GONE
            }

            //Setting the click listener for the Save card icon. this Adds the card to the task
            holder.itemView.ib_done_card_name.setOnClickListener {
                val cardName = holder.itemView.et_card_name.text.toString()

                if (cardName.isNotEmpty()) {
                    if (context is TasksListActivity) {
                        context.addCardToTaskList(position, cardName)
                    }
                } else {
                    Toast.makeText(
                        context, "Please enter a Card name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            //Configuring the cards recycler view
            holder.itemView.rv_card_list.layoutManager = LinearLayoutManager(context)
            holder.itemView.rv_card_list.setHasFixedSize(true)
            val adapter = CardListItemsAdapter(context, model.cards)
            holder.itemView.rv_card_list.adapter = adapter


        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**This method gets the DP of the screen from the Pixel and then we can use it accordingly*/
    private fun Int.toDp() : Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    /**This method gets the Pixel of the screen frm the Dp and then we can use it accordingly*/
    private fun Int.toPx() : Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    /**Method is used to show the Alert Dialog for deleting the task list.*/
    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
/*
        val builder = androidx.appcompat.app.AlertDialog.Builder(context,
            R.style.AlertDialogTheme)*/

        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TasksListActivity) {
                context.deleteTaskList(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }
}