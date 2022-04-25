package com.example.plana22

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.plana22.RoomDetail.DetailApp
import com.example.plana22.RoomDetail.DetailEntity
import com.example.plana22.RoomDetail.TaskList
import kotlinx.android.synthetic.main.add_category_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import android.app.Activity
import android.app.Application
import android.os.Looper.*
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext


class OverviewViewModel(val context : Context,
                        application: Application,
                        val detailApp: DetailApp) : AndroidViewModel(application) {

    val categoryLiveData = MutableLiveData<ArrayList<DetailEntity>>()

    @SuppressLint("StaticFieldLeak")

    fun toastMessage() {
        return Toast.makeText(
            context,
            " This Category already exists", Toast.LENGTH_LONG
        ).show()
    }


    /**Block of code that has the logic in the View Model that'll Add the Category */
    fun addCategoryDialogVMN() {
        val addDialog = Dialog(context, R.style.Theme_Dialog)
        addDialog.setContentView(R.layout.add_category_dialog)
        addDialog.show()
        addDialog.setCancelable(false)

        //Initialising the category array in the strings file
        val category = context.resources.getStringArray(R.array.Category)

        //Joining the array so it can come out in the dropdown_item format(Like recyclerView stuff)
        val arrayAdapter = ArrayAdapter(context, R.layout.dropdown_item, category)

        //Joining the autoCompleteTextView which is the dropdown edit text with the adapter
        addDialog.autoCompleteTextView.setAdapter(arrayAdapter)

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

                    viewModelScope.launch(Dispatchers.IO) {
                        //Initialising the exists() from the DetailDao
                        val exists = detailApp.checkIfExistsRepo(
                            addDialog
                                .autoCompleteTextView.text.toString()
                        )

                        //Check if the category(or string) already exists
                        if(exists){

                            withContext(Main){
                                toastMessage()
                            }

                        } else {

                            //Inserts the category into the Database
                            detailApp.addCategoryRepo(
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
                    viewModelScope.launch(Dispatchers.IO) {
                        //Initialising the exists() from the DetailDao
                        val exists =
                            detailApp.checkIfExistsRepo(addDialog
                                .autoCompleteTextView.text.toString())

                        //Check if the category(or string) already exists
                        if (exists) {
                            withContext(Main){
                                toastMessage()
                            }

                        } else {
                            //Inserts the category into the Database
                            detailApp.addCategoryRepo(
                                DetailEntity(
                                    image = R.drawable.blackboycopy,
                                    category = "Personal",
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
                    viewModelScope.launch(Dispatchers.IO) {
                        //Initialising the exists() from the DetailDao
                        val exists =
                            detailApp.checkIfExistsRepo(addDialog
                                .autoCompleteTextView.text.toString())

                        //Check if the category(or string) already exists
                        if (exists) {
                            withContext(Main){
                                toastMessage()
                            }

                        } else {
                            //Inserts the category into the Database
                            detailApp.addCategoryRepo(
                                DetailEntity(
                                    image = R.drawable.bluecalendarcopy,
                                    category = "Planned",
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

                    viewModelScope.launch(Dispatchers.IO) {
                        //Initialising the exists() from the DetailDao
                        val exists =
                            detailApp.checkIfExistsRepo(addDialog
                                .autoCompleteTextView.text.toString())

                        //Check if the category(or string) already exists
                        if (exists) {
                            withContext(Main){
                                toastMessage()
                            }

                        } else {
                            //Inserts the category into the Database
                            detailApp.addCategoryRepo(
                                DetailEntity(
                                    image = R.drawable.newsuitcasecopy3,
                                    category = "Work",
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

                    viewModelScope.launch(Dispatchers.IO) {

                        //Initialising the exists() from the DetailDao
                        val exists =
                            detailApp.checkIfExistsRepo(addDialog
                                .autoCompleteTextView.text.toString())

                        //Check if the category(or string) already exists
                        if (exists) {
                            withContext(Main){
                                toastMessage()
                            }

                        } else {
                            //Inserts the category into the Database
                            detailApp.addCategoryRepo(
                                DetailEntity(
                                    image = R.drawable.blueshopcartcopy,
                                    category = "Shopping",
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
                    toastMessage()
                }
            }
        }

        /**What happens when you click on the Negative button*/

        addDialog.tvCancel.setOnClickListener {
            addDialog.dismiss()
        }

    }

}

