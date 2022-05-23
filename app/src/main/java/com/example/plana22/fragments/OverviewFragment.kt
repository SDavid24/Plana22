package com.example.plana22.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.plana22.Adapters.OverviewAdapter
import com.example.plana22.MVVM.OverviewViewModel
import com.example.plana22.MVVM.OverviewViewModelFactory
import com.example.plana22.Models.User
import com.example.plana22.R
import com.example.plana22.RoomDetail.DetailApp
import com.example.plana22.RoomDetail.DetailDao
import com.example.plana22.RoomDetail.DetailEntity
import com.example.plana22.firebase.FireStoreClass
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OverviewFragment : Fragment(
    R.layout.fragment_overview
) {

    //val activity  = MainActivity()
    lateinit var viewModel : OverviewViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val detailApp = DetailApp()
        val detailDao = (requireActivity().application as DetailApp).db.detailDao()

        updateUserDataInOverview(User())

        //FireStoreClass().loadUserDataInFragment(this)

        //Loads the data from the database into the frament UI
        FireStoreClass().readDatabaseAndLoadUserDataToFragment(this)

        val factory = OverviewViewModelFactory(requireContext(), detailApp)

        viewModel = ViewModelProvider(requireActivity(), factory).get(OverviewViewModel::class.java)

        /**Coroutine that helps the room database setup the data into the recyclerview */
        lifecycleScope.launch{
            detailDao.fetchAllTaskCategory().collect {
                val list = ArrayList(it)

                generateRecyclerview(list, detailDao)
            }
        }

        addCategory()

    }

    /**Function that retrieves the code that has the logic behind this operationfrom the View model*/
    fun addCategory(){
        fabAddCategoryFrag.setOnClickListener {
            viewModel.categoryLiveData.observe(this, Observer { category->
                if(it == null) {
                    Toast.makeText(requireContext(), "Failed to create new category...", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Successfully created/updated user...", Toast.LENGTH_LONG).show()

                    activity!!.finish()
                }
            })

            viewModel.addCategoryDialogInVM()

        }

    }

    /** Method to set up the recyclerViewList on the screen*/
    private fun generateRecyclerview(
        overviewList: ArrayList<DetailEntity>, detailDao: DetailDao
    ) : ArrayList<DetailEntity> {

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
    }


    /**A function to get the current user details from firebase and set the name in the UI ti the current user's first name.*/
    fun updateUserDataInOverview(user: User){
        "Hello ${user.firstName}".also { hello_name_text.text = it }

    }

    companion object {
        val DETAIL_ACTIVITY_REQUEST_CODEE = 1
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





}
