package com.example.plana22

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plana22.Activities.DetailActivity
import com.example.plana22.Activities.OverviewActivity
import com.example.plana22.Activities.OverviewActivity.Companion.EXTRA_TASK_DETAILS
import com.example.plana22.Adapters.OverviewAdapter
import com.example.plana22.RoomDetail.*
import com.example.plana22.databinding.ActivityDetailBinding.inflate
import com.example.plana22.databinding.AddCategoryDialogBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_overview.*
import kotlinx.android.synthetic.main.add_category_dialog.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OverviewFragment : Fragment(
    R.layout.fragment_overview) {

    val detailFragment = DetailFragment()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val detailDao = DetailDatabase.getInstance(requireContext()).detailDao()
        val detailDao = (requireActivity().application as DetailApp).db.detailDao()

        /**Coroutine that helps the room database setup the data into the recyclerview */
        lifecycleScope.launch{
            detailDao.fetchAllTaskCategory().collect {
                val list = ArrayList(it)

                generateRecyclerview(list, detailDao)
            }
        }

        fabAddCategoryFrag.setOnClickListener {
            addCategoryDialog(detailDao)
        }
    }

    /** Method to set up the recyclerViewList on the screen*/
    private fun generateRecyclerview(
        overviewList: ArrayList<DetailEntity>, detailDao: DetailDao
    ) : ArrayList<DetailEntity>
    {

        ///rv_overview_fragment.setHasFixedSize(true)
        //rv_overview_fragment.layoutManager = LinearLayoutManager(requireContext())
        //rv_overview_fragment.layoutManager = LinearLayoutManager(requireContext())

        val overviewAdapter = OverviewAdapter(requireActivity(), overviewList, detailDao)

        rv_overview_fragment.adapter = overviewAdapter

        /**method to ensure every row in the recyclerview that's clicked
         * links to the detail page*/
        overviewAdapter.setOnClickListener(object : OverviewAdapter.OnClickListener{
            override fun onClick(position: Int, model: DetailEntity) {

                val bundle = Bundle()
                bundle.putParcelable(EXTRA_TASK_DETAILS, model )
                detailFragment.arguments = bundle

                parentFragmentManager.beginTransaction().apply {
                    addToBackStack(null)
                    replace(R.id.flFragment, detailFragment)
                    commit()
                }
            }
        })

        return overviewList
    }


    /**Method to insert the details in a row using a Dialog picker*/
    fun addCategoryDialog(detailDao: DetailDao) {
        val binding = AddCategoryDialogBinding.inflate(layoutInflater)
        val addDialog = Dialog(requireContext(), R.style.Theme_Dialog)
        addDialog.setContentView(binding.root)
        addDialog.show()
        addDialog.setCancelable(false)

        //Initialising the category array in the strings file
        val category = resources.getStringArray(R.array.Category)
        //Joining the array so it can come out in the dropdown_item format(Like recyclerView stuff)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, category)

        //Joining the autoCompleteTextView which is the dropdown edit text with the adapter
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        /**Determining what should happen if the customer clicks on yes after picking a category which in this case is to Add a category to the Database and also display in the RecyclerView
         */
        addDialog.tvAddCategory.setOnClickListener {
            val initTaskList =
                mutableListOf<TaskList>() //Initializing the taskList to a mutable empty list

            /**A conditional that firstly checks the Category column in the Room Database
             *  if a chosen category is already present before it either adds it to
             *  the database or rejects it.*/
            when {
                //For the Today category
                addDialog.autoCompleteTextView.text.toString() == "Today" -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        //Initialising the exists() from the DetailDao
                        val exists = detailDao.exists(addDialog.autoCompleteTextView.text.toString())

                        //Check if the category(or string) already exists
                        if(exists){
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(),
                                    " This Category already exists",
                                    Toast.LENGTH_LONG).show()
                            }
                        }else {

                            //Inserts the category into the Database
                            detailDao.insert(
                                DetailEntity(
                                    image = R.drawable.yellowsun, category = "Today",
                                    taskAmount = 0,
                                    taskList = initTaskList
                                )
                            )
                            addDialog.dismiss()
                        }
                    }
                }

                //For the Personal category
                addDialog.autoCompleteTextView.text.toString() == "Personal" -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        //Initialising the exists() from the DetailDao
                        val exists = detailDao.exists(addDialog.autoCompleteTextView.text.toString())

                        //Check if the category(or string) already exists
                        if(exists){
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(),
                                    " This Category already exists",
                                    Toast.LENGTH_LONG).show()
                            }

                        }else {
                            //Inserts the category into the Database
                            detailDao.insert(
                                DetailEntity(
                                    image = R.drawable.blackboycopy, category = "Personal",
                                    taskAmount = 0,
                                    taskList = initTaskList
                                )
                            )
                            addDialog.dismiss()
                        }
                    }

                }

                //For the Planned category
                addDialog.autoCompleteTextView.text.toString() == "Planned" -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        //Initialising the exists() from the DetailDao
                        val exists = detailDao.exists(addDialog.autoCompleteTextView.text.toString())

                        //Check if the category(or string) already exists
                        if(exists){
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(),
                                    " This Category already exists",
                                    Toast.LENGTH_LONG).show()
                            }
                        }else {
                            //Inserts the category into the Database
                            detailDao.insert(
                                DetailEntity(
                                    image = R.drawable.bluecalendarcopy, category = "Planned",
                                    taskAmount = 0,
                                    taskList = initTaskList
                                )
                            )
                            addDialog.dismiss()

                        }
                    }
                }

                //For the Work category
                addDialog.autoCompleteTextView.text.toString() == "Work" -> {

                    lifecycleScope.launch(Dispatchers.IO) {
                        //Initialising the exists() from the DetailDao

                        val exists = detailDao.exists(addDialog.autoCompleteTextView.text.toString())

                        //Check if the category(or string) already exists
                        if(exists){
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(),
                                    " This Category already exists",
                                    Toast.LENGTH_LONG).show()
                            }

                        }else {
                            //Inserts the category into the Database
                            detailDao.insert(
                                DetailEntity(
                                    image = R.drawable.newsuitcasecopy3, category = "Work",
                                    taskAmount = 0,
                                    taskList = initTaskList
                                )
                            )
                            addDialog.dismiss()
                        }
                    }
                }

                //For the Shopping category
                addDialog.autoCompleteTextView.text.toString() == "Shopping" -> {

                    lifecycleScope.launch(Dispatchers.IO) {

                        //Initialising the exists() from the DetailDao
                        val exists = detailDao.exists(addDialog.autoCompleteTextView.text.toString())

                        //Check if the category(or string) already exists
                        if(exists){
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(),
                                    " This Category already exists",
                                    Toast.LENGTH_LONG).show()
                            }

                        }else {
                            //Inserts the category into the Database
                            detailDao.insert(
                                DetailEntity(
                                    image = R.drawable.blueshopcartcopy, category = "Shopping",
                                    taskAmount = 0,
                                    taskList = initTaskList
                                )
                            )
                            addDialog.dismiss()
                        }
                    }
                }

                //if a null option is by any means picked
                else -> {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(),
                            " This Category already exists",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        /**What happens when you click on the Negative button*/
        addDialog.tvCancel.setOnClickListener {
            addDialog.dismiss()

        }

    }


    /**
     * Delete dialog to display when an item is clicked on in the recycler view
     * And also tries to carry out the delete whole  category function*/
    fun deleteRecordDialog(id:Int, detailDao: DetailDao) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        builder.setCancelable(false)
        builder.setTitle("Delete category")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        /**This decides what should happen when we click on the "Yes" button*/
        builder.setPositiveButton("Yes") { dialogInterface, _ ->

            lifecycleScope.launch {
                detailDao.delete(DetailEntity(id)) //The main that is in charge of deleting the category
                Toast.makeText(
                    requireContext(),
                    "Record deleted successfully", Toast.LENGTH_LONG
                ).show()
            }
            dialogInterface.dismiss()
        }

        /**This decides what should happen when we click on the "No" button*/
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        builder.show()
    }


    companion object {
        val DETAIL_ACTIVITY_REQUEST_CODE = 1
        val EXTRA_TASK_DETAILS = "extra task details"
    }
}