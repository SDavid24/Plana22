package com.example.plana22.Activities.operations

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.plana22.Activities.introduction.BaseActivity
import com.example.plana22.Adapters.CardMemberListItemAdapter
import com.example.plana22.Models.Board
import com.example.plana22.Models.Card
import com.example.plana22.Models.Task
import com.example.plana22.Models.User
import com.example.plana22.R
import com.example.plana22.dialogs.LabelColorListDialog
import com.example.plana22.dialogs.MembersListDialog
import com.example.plana22.firebase.FireStoreClass
import com.example.plana22.utils.Constants
import kotlinx.android.synthetic.main.activity_card_details.*
import kotlinx.android.synthetic.main.item_task.*
import models.SelectedMembers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {
    private lateinit var mBoardDetails : Board
    private var mTaskPosition  = -1
    private var mCardPosition =  -1
    private var mSelectedColor: String = ""
    private lateinit var mMembersDetailsList: ArrayList<User>
    private var mSelectedDueDateMilliSeconds : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        getIntentData()

        setupActionBar()
    }

    /**A function to setup action bar*/
    private fun setupActionBar() {
        setSupportActionBar(toolbar_card_details_activity)

        val actionBar = supportActionBar

        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

            actionBar.title = mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name
        }

        et_name_card_details.setText(mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name)
        et_name_card_details.setSelection(mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name.length)

        //Making the "back" able to take the activity back to the previous activity
        toolbar_card_details_activity.setNavigationOnClickListener {
            onBackPressed()
        }


        btn_update_card_details.setOnClickListener {
            if (et_name_card_details.text.toString().isNotEmpty()){
                updateCardDetails()
            }else{
                showSnackBar("Please enter a card name")
            }
        }


        tv_select_label_color.setOnClickListener {
            labelColorDialog()
        }

        tv_select_members.setOnClickListener{
            membersListDialog()
        }

        //Setting up the members list recycler view
        setUpSelectedMembersList()


        mSelectedDueDateMilliSeconds = mBoardDetails.taskList[mTaskPosition]
            .cards[mCardPosition].dueDate

        if (mSelectedDueDateMilliSeconds > 0){
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(mSelectedDueDateMilliSeconds))

            tv_select_due_date.text = selectedDate
        }

        tv_select_due_date.setOnClickListener {
            showDatePicker()
        }

    }

    /**A function to get all the data that is sent through intent.*/
    private fun getIntentData(){
        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,  -1,)
        }

        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,  -1,)
        }

        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailsList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    /**Function to set the new detail(i.e the new edited card name) as the new card detail */
    fun updateCardDetails(){
        val card = Card(
            et_name_card_details.text.toString(),
            mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDateMilliSeconds
        )

        mBoardDetails.taskList[mTaskPosition].cards[mCardPosition] = card

        showProgressDialog((resources.getString(R.string.please_wait)))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)

    }

    //Creates an options menu for linking to the Members activity when it is clicked
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when(item.itemId){
            R.id.action_delete_card -> {
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskPosition].cards[mCardPosition].name)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /** A function to delete the card from the task list.*/
    private fun deleteCard(){
        val cardList: ArrayList<Card> = mBoardDetails.taskList[mTaskPosition].cards
        cardList.removeAt(mCardPosition)

        val taskList  : ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(mBoardDetails.taskList.size - 1)

        taskList[mTaskPosition].cards = cardList

        showProgressDialog((resources.getString(R.string.please_wait)))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    /** A function to show an alert dialog for the confirmation to delete the card.*/
    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.alert))

        //set message for alert dialog
        //builder.setMessage("Are you sure you want to delete $cardName.")
        builder.setMessage(resources.getString(R.string.confirmation_message_to_delete_card))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            deleteCard()

            finish()
        }

        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    /**A function to add some static label colors in the list.*/
    fun colorList(): ArrayList<String>{
        val colorsList : ArrayList<String> = ArrayList()

        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }


    /**A function to remove the text and set the label color to the TextView.*/
    private fun setColor() {
        tv_select_label_color.text = ""
        tv_select_label_color.setBackgroundColor(Color.parseColor(mSelectedColor))

    }


    /**
     * A function to launch the label color list dialog.
     * */
    fun labelColorDialog(){
        //instantiating the colorList method so i can be used as a parameter in this method
        val colorsList = colorList()

        //Pass the selected color to show it as already selected with tick icon in the list
        val list = object : LabelColorListDialog(this,
            colorsList, resources.getString(R.string.select_color),
            mSelectedColor
        ){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }

        list.show()

    }


    /**A function to launch the  members list dialog.*/

    private fun membersListDialog(){
        // Here we get the updated assigned members list
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskPosition]
            .cards[mCardPosition].assignedTo

        //Check if the list has at least one member so you can operate on it
        if(cardAssignedMembersList.size > 0 ){
            // Here we got the details of assigned members list from the global members list which is passed from the Task List screen.
            for(i in mMembersDetailsList.indices){
                for (j in cardAssignedMembersList){
                    if (mMembersDetailsList[i].id == j){//If a board member details equal that of card member, then we say it has been selected.

                        mMembersDetailsList[i].selected = true
                    }
                }
            }

        }else{
            for(i in mMembersDetailsList.indices){
                mMembersDetailsList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(this,
            mMembersDetailsList, resources.getString(R.string.select_members)){

            //Click listener if any of the items are selected.
            override fun onItemSelected(user: User, action: String) {
                //Here, the action is to select.
                if (action == Constants.SELECT){
                    // If the user is not previously selected, i.e the assigned card member list does not contain the user,
                    if(!mBoardDetails.taskList[mTaskPosition]
                            .cards[mCardPosition].assignedTo.contains(user.id)){

                        //Add the user to the Card member list
                        mBoardDetails.taskList[mTaskPosition]
                            .cards[mCardPosition].assignedTo.add(user.id)
                    }else{
                        //If it was previously selected, remove the user to the Card member list
                        mBoardDetails.taskList[mTaskPosition]
                            .cards[mCardPosition].assignedTo.remove(user.id)

                        //Converts the user selected status to False
                        for (i in mMembersDetailsList.indices){
                            if (mMembersDetailsList[i].id == user.id){
                                mMembersDetailsList[i].selected = false
                            }
                        }

                    }
                    setUpSelectedMembersList()
                }
            }

        }
        listDialog.show()
    }

    /**
     * A function to launch and setup assigned members detail list into recyclerview.
     */

    /**Sets up the recycler of the selected members list*/
    private fun setUpSelectedMembersList(){
        //Initialises the members that have been assigned to the card
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskPosition]
            .cards[mCardPosition].assignedTo

        //Instantiates an empty array list to store members that have been assigned to the card
        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for (i in mMembersDetailsList.indices){
            for (j in cardAssignedMembersList){
                if (mMembersDetailsList[i].id == j){ //If a board member details equal that of card member, then we say it has been selected.

                    //Then initialises the SelectedMembers object, so we reuse it
                    val selectedMembers = SelectedMembers(
                        mMembersDetailsList[i].id,
                        mMembersDetailsList[i].image
                    )

                    //Finally we add the selectedMembers objects to the already created array list
                    selectedMembersList.add(selectedMembers)
                }
            }
        }

        //Things that should happen if the card members list has at least 1 selected member
        if(selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("", "")) //Add an empty member that the Add Member image button can occupy
            tv_select_members.visibility = View.GONE //Hide the select member text view
            rv_selected_members_list.visibility = View.VISIBLE //Show the selected members recycler view
            //Configuration of the recycler view
            rv_selected_members_list.layoutManager = GridLayoutManager(this, 5)

            val adapter = CardMemberListItemAdapter(this, selectedMembersList, true)
            rv_selected_members_list.adapter = adapter

            rv_selected_members_list.setHasFixedSize(true)

            //When anyone is clicked on, It shows the members list dialog
            adapter.setOnClickListener(object : CardMemberListItemAdapter.OnClickListener{
                override fun onClick() {
                    membersListDialog()
                    //labelColorDialog()
                }

            })

        }else{
            tv_select_members.visibility = View.VISIBLE  //Show the select member text view
            rv_selected_members_list.visibility = View.GONE //hide the selected members recycler view
        }
    }


    /**Sets up the recycler of the selected members list*/
/*
    private fun setUpSelectedMembersList() {
        //Initialises the members that have been assigned to the card
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskPosition]
            .cards[mCardPosition].assignedTo

        //Instantiates an empty array list to store members that have been assigned to the card
        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for (i in mMembersDetailsList.indices) {
            for (j in cardAssignedMembersList) {
                if (mMembersDetailsList[i].id == j) { //If a board member details equal that of card member, then we say it has been selected.

                    //Then initialises the SelectedMembers object, so we reuse it
                    val selectedMembers = SelectedMembers(
                        mMembersDetailsList[i].id, mMembersDetailsList[i].image
                    )

                    //Finally we add the selectedMembers objects to the already created array list
                    selectedMembersList.add(selectedMembers)
                }
            }
        }

        //Things that should happen if the card members list has at least 1 selected member
        if(selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("", "")) //Add an empty member that the Add Member image button can occupy
            tv_select_members.visibility = View.GONE //Hide the select member text view
            rv_selected_members_list.visibility = View.VISIBLE //Show the selected members recycler view
            //Configuration of the recycler view
            rv_selected_members_list.layoutManager = GridLayoutManager(this, 5)

            val adapter = CardMemberListItemAdapter(this, selectedMembersList, true)
            rv_selected_members_list.adapter = adapter

            rv_selected_members_list.setHasFixedSize(true)

            //When anyone is clicked on, It shows the members list dialog
            adapter.setOnClickListener(object : CardMemberListItemAdapter.OnClickListener{
                override fun onClick() {
                    membersListDialog()
                }
            })
        }else{
            tv_select_members.visibility = View.VISIBLE  //Show the select member text view
            rv_selected_members_list.visibility = View.GONE //hide the selected members recycler view
        }
    }
*/


    /**
     * The function to show the DatePicker Dialog and select the due date.
     */
    private fun showDatePicker() {
        /**
         * This Gets a calendar using the default time zone and locale.
         * The calender returned is based on the current time
         * in the default time zone with the default.
         */
        val c = Calendar.getInstance()
        val year =
            c.get(Calendar.YEAR) // Returns the value of the given calendar field. This indicates YEAR
        val month = c.get(Calendar.MONTH) // This indicates the Month
        val day = c.get(Calendar.DAY_OF_MONTH) // This indicates the Day

        /**
         * Creates a new date picker dialog for the specified date using the parent
         * context's default date picker dialog theme.
         */
        val dpd = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                /*
                 Here the selected date is set into format i.e : day/Month/Year
                  And the month is counted in java is 0 to 11 so we need to add +1
                  so it can be as selected.*/

                // Here we have appended 0 if the selected day/month is smaller than 10 to make
                // it double digit value.
                val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"

                val sMonthOfYear =
                    if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"

                // Selected date it set to the TextView to make it visible to user.
                tv_select_due_date.text = selectedDate

                /**
                 * Here we have taken an instance of Date Formatter as it will format our
                 * selected date in the format which we pass it as an parameter and Locale.
                 * Here I have passed the format as dd/MM/yyyy.
                 */
                /**
                 * Here we have taken an instance of Date Formatter as it will format our
                 * selected date in the format which we pass it as an parameter and Locale.
                 * Here I have passed the format as dd/MM/yyyy.
                 */
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                // The formatter will parse the selected date in to Date object
                // so we can simply get date in to milliseconds.
                val theDate = sdf.parse(selectedDate)

                /** Here we get the time in milliSeconds from Date object*/

                /** Here we get the time in milliSeconds from Date object*/

                mSelectedDueDateMilliSeconds = theDate!!.time
            },
            year,
            month,
            day
        )

        dpd.show() // It is used to show the datePicker Dialog.
    }

    /**A function to get the result of add or updating the task list.*/
    fun addUpdateCardListSuccess() {
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }
}