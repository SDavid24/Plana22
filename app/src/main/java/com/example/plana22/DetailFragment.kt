package com.example.plana22

import android.app.Dialog
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plana22.Activities.OverviewActivity
import com.example.plana22.Adapters.DetailAdapter
import com.example.plana22.OverviewFragment.Companion.EXTRA_TASK_DETAILS
import com.example.plana22.RoomDetail.DetailApp
import com.example.plana22.RoomDetail.DetailDao
import com.example.plana22.RoomDetail.DetailEntity
import com.example.plana22.RoomDetail.TaskList
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.add_task_dialog.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailFragment : Fragment(R.layout.fragment_detail) {
    private  var detailActivityModel: DetailEntity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //detailActivityModel = DetailEntity()
        val bundle = arguments
        val detailDao = (requireActivity().application as DetailApp).db.detailDao()

        if(bundle != null){

            detailActivityModel = bundle.getParcelable(EXTRA_TASK_DETAILS)

            //Sets the page image to the category's image that's clicked on
            detail_page_image.setImageResource(detailActivityModel!!.image!!)

            //Sets the page header to the name of the category's that's clicked on
            detail_page_header.text = detailActivityModel!!.category

            //Brings in the ID of the category into this activity which can
            //be used for further purposes
            objectID.text = detailActivityModel!!.id.toString()
            val numba = objectID.text.toString().toInt()
            Toast.makeText(requireContext(),"Object ID is $numba", Toast.LENGTH_LONG).show()

        }

        val trueValue = objectID.text.toString()
        Log.i("New Id", trueValue) //Displays the ID of the category that's clicked on in the log

        //actionBar?.setDisplayShowTitleEnabled(false)   //code to remove title rom the action bar

        rv_detail.setHasFixedSize(true)

        /*Event clickListener for the Fab button. It opens up the Add Task dialog*/
        fabAddTask.setOnClickListener {
            addCategoryDialog(activityObjectID(), detailDao)
        }

        /**Coroutine that calculates the amount of data inserted
         * into a category and displays it immediately */
        lifecycleScope.launch{
            detailDao.fetchTaskCategoryById(activityObjectID()).collect {
                //Initializing the taskList to the  original taskList of the chosen category
                val taskList = it.taskList
                taskCount(taskList)  //applying the taskCount function
            }
        }

        setupListOfDataIntoRecyclerView(
            detailActivityModel!!.taskList
                as ArrayList<TaskList>, detailDao)

        //calling the setup recycler view function

    }


    /**
     * Function is used show the list of inserted data.
     */
    private fun setupListOfDataIntoRecyclerView(list : ArrayList<TaskList>, detailDao: DetailDao) {

        val newTaskList = list

        if (newTaskList.isNotEmpty()) {

            Log.i("Checker", "This line makes sense")
            rv_detail.visibility = View.VISIBLE
            blankPageText.visibility = View.GONE

            // Set the LayoutManager that this RecyclerView will use.
            rv_detail.layoutManager = LinearLayoutManager(requireContext())

            // Adapter class is initialized and list is passed in the param.
            val detailAdapter = DetailAdapter(requireContext(), detailDao)
            // adapter instance is set to the recyclerview to inflate the items.
            rv_detail.adapter = detailAdapter
            detailAdapter.setListData(newTaskList)

            taskText.text = "${detailAdapter.items.count()} Tasks"

            Log.i("Lists of tasks for ${detailActivityModel!!.category}", newTaskList.toString())

        } else {
            Log.i("Checker", "This line DOESN'T make sense")

            rv_detail.visibility = View.GONE
            blankPageText.visibility = View.VISIBLE
            Log.i("Guess what?", "Task is empty!")

        }
    }


    private fun addCategoryDialog(id: Int, detailDao : DetailDao) {
        val addDialog = Dialog(requireContext(), R.style.Theme_Dialog)
        addDialog.setContentView(R.layout.add_task_dialog)
        addDialog.show()
        addDialog.setCancelable(false)  //to prevent dismissing the dialog when outside of it clicked


        /**Event listener for Add Task button*/
        addDialog.tvAddTASK.setOnClickListener {

            val task = addDialog.etAddTaskName.text.toString()
            val taskList = mutableListOf<TaskList>()

            if (task.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Task cannot be blank", Toast.LENGTH_SHORT).show()
            }else {
                try {

                    taskList.add(TaskList(task))
                    //Adding the entry to the task list of the category in question
                    detailActivityModel?.taskList?.add(TaskList(task))
                }catch (e : Exception){
                    Toast.makeText(
                        requireContext(), "There's ADD CATEGORY problem!!!",
                        Toast.LENGTH_SHORT).show()
                }
                try {
                    //Using coroutine to update the entry in the database
                    lifecycleScope.launch {
                        detailDao.update(detailActivityModel!!)
                        // taskAmountUpdate(detailDao)  //updating the task amount in the database
                    }
                }catch (e : Exception){

                    Toast.makeText(
                        requireContext(), "There's UPDATE problem!!!",
                        Toast.LENGTH_SHORT).show()
                }



                Toast.makeText(
                    requireContext(), "Task added", Toast.LENGTH_SHORT).show()
                addDialog.etAddTaskName.text.clear()  //Clear the textEdit space after adding entry

                try {


                    //Update recycler view immediately after an entry
                    setupListOfDataIntoRecyclerView(
                        detailActivityModel!!.taskList as ArrayList<TaskList>, detailDao
                    )
                }catch (e: Exception){

                    Toast.makeText(
                        requireContext(), "There's SETUP problem!!!",
                        Toast.LENGTH_SHORT).show()
                }

                addDialog.dismiss()  //dismiss the dialog
            }

        }

        /**Event listener for Cancel button*/
        addDialog.tvCancel.setOnClickListener {
            addDialog.dismiss() //dismiss the dialog
        }

    }

    /**Method to display the exact amount of tasks*/
    private fun taskCount(taskList: MutableList<TaskList>) : Int{
        //Initializing count to count function which COUNTS the number of tasks entry in a category
        val count : Int = taskList.count()
        taskNumber.text  = count.toString()
        //detailActivityModel!!.taskAmount = taskNumber.text.toString().toInt() + 1

        //detailActivityModel!!.taskAmount = taskList.count() + 1 //equating taskAmount from the entity to the the taskList count

        //Conditional to display the correct word(task) regarding the amount of tasks
        if(count == 0 || count == 1) {
            taskText.text = getString(R.string.singular_task)

        }else{
            taskText.text = getString(R.string.plural_tasks)
        }

        return count
    }

    fun activityObjectID(): Int {
        return objectID.text.toString().toInt()
    }

    /**Method to Delete the details in a  using an Alert Dialog*/
    fun deleteRecordDialog(id:Int, detailDao: DetailDao) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(
            requireContext(), R.style.AlertDialogTheme)
        builder.setCancelable(false)
        builder.setTitle("Delete Record")

        builder.setIcon(android.R.drawable.ic_dialog_alert)

        /**This decides what should happen when we click on the "Yes" button*/
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            lifecycleScope.launch {
                detailActivityModel!!.taskList.removeAt(id)

                detailDao.update(detailActivityModel!!)

            }

            Toast.makeText(
                requireContext(),
                "Record deleted successfully", Toast.LENGTH_LONG
            ).show()
            dialogInterface.dismiss()
        }

        /**This decides what should happen when we click on the "No" button*/
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        builder.show()

    }
}