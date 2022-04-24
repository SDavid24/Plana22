package com.example.plana22

import android.app.Dialog
import android.content.ContentProvider
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.plana22.Adapters.OverviewAdapter
import com.example.plana22.RoomDetail.DetailApp
import com.example.plana22.RoomDetail.DetailDao
import com.example.plana22.RoomDetail.DetailEntity
import com.example.plana22.RoomDetail.TaskList
import com.example.plana22.databinding.AddCategoryDialogBinding
import kotlinx.android.synthetic.main.add_category_dialog.*
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch



class OverviewFragment : Fragment(
    R.layout.fragment_overview) {




    lateinit var viewModel : OverviewViewModel



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val detailApp = DetailApp()
        val detailDao = (requireActivity().application as DetailApp).db.detailDao()

        val factory = OverviewViewModelFactory(requireContext(), detailApp)


        viewModel = ViewModelProvider(requireActivity(), factory).get(OverviewViewModel::class.java)




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
    ) : ArrayList<DetailEntity> {

        ///rv_overview_fragment.setHasFixedSize(true)
        //rv_overview_fragment.layoutManager = LinearLayoutManager(requireContext())
        //rv_overview_fragment.layoutManager = LinearLayoutManager(requireContext())

        val overviewAdapter = OverviewAdapter(requireActivity(), overviewList, detailDao)

        rv_overview_fragment.adapter = overviewAdapter
        /**method to ensure every row in the recyclerview that's clicked
         * links to the detail page*/
        overviewAdapter.setOnClickListener(object : OverviewAdapter.OnClickListener{
            override fun onClick(position: Int, model: DetailEntity) {

                val detailFragment = DetailFragment()
                val bundle = Bundle()
                bundle.putParcelable(EXTRA_TASK_DETAILS, model )
                detailFragment.arguments = bundle

                //switches fragment when you click on any of the recyclerviews
                parentFragmentManager.beginTransaction().apply {
                    addToBackStack(null)
                    replace(R.id.flFragment, detailFragment)
                    commit()
                }

            }
        })

        return overviewList
    }



    /**ATTENTION NEEDED HERE!!!
     * This method works perfectly fine without the View model interference
     * But when you try to use the view model, it crashes
     * Method to insert the details in a row using a Dialog picker*/
    fun addCategoryDialog(detailDao: DetailDao) {
        val binding = AddCategoryDialogBinding.inflate(layoutInflater)
        val addDialog = Dialog(requireContext(), R.style.Theme_Dialog)
        //val addDialog = .addDialog
        addDialog.setContentView(binding.root)
        addDialog.show()
        addDialog.setCancelable(false)
        //viewModel = ViewModelProvider(this).get(OverviewViewModel::class.java)

        //Initialising the category array in the strings file
        val category = resources.getStringArray(R.array.Category)
        //Joining the array so it can come out in the dropdown_item format(Like recyclerView stuff)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, category)

        //Joining the autoCompleteTextView which is the dropdown edit text with the adapter
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        /**Determining what should happen if the customer clicks on yes after picking a category which in this case is to Add a category to the Database and also display in the RecyclerView
         */
        addDialog.tvAddCategory.setOnClickListener {


            //Code that should call the code  the logic behind the Add category method
            // from the View model
       /*     viewModel.categoryLiveData.observe(this, Observer { category->
                if(it == null) {
                    Toast.makeText(requireContext(), "Failed to create new category...", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Successfully created/updated user...", Toast.LENGTH_LONG).show()

                    activity!!.finish()
                }
            })

            viewModel.addCategoryVM()
*/

            //CODE FOR THE NORMAL ADD CATEGORY METHOD WHERE THE LOGIC IS ALSO INSIDE FRAGMENT
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
     * THIS DELETE DIALOG NEEDS ATTENTION!!!.
     * App crashes when you try to delete a category
     * Delete dialog to display when an item is clicked on in the recycler view
     * And also tries to carry out the delete whole  category function*/
    fun deleteRecordDialog(id:Int, detailDao: DetailDao) {
        //checkIfFragmentAttached {

        val builder = androidx.appcompat.app.AlertDialog.Builder(
            requireContext(),
            R.style.AlertDialogTheme
        )
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
        //}
    }

    companion object {
        val DETAIL_ACTIVITY_REQUEST_CODE = 1
        val EXTRA_TASK_DETAILS = "extra task details"
    }


    /**--------------------------------------------------------------------*/
    /**You can choose to ignore this*/
    /*
    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
        else{
            Log.e("Problem", " Check here!!")
        }

*/

    fun addCategory(){
        fabAddCategoryFrag.setOnClickListener {
            /*     viewModel.categoryLiveData.observe(this, Observer { category->
         if(it == null) {
             Toast.makeText(requireContext(), "Failed to create new category...", Toast.LENGTH_LONG).show()
         } else {
             Toast.makeText(requireContext(), "Successfully created/updated user...", Toast.LENGTH_LONG).show()

             activity!!.finish()
         }
     })

     viewModel.addCategoryVM()
*/

        }
    }


}