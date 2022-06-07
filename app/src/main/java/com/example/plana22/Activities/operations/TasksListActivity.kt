package com.example.plana22.Activities.operations

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plana22.Activities.introduction.BaseActivity
import com.example.plana22.Adapters.TaskListItemsAdapter
import com.example.plana22.Models.Board
import com.example.plana22.Models.Card
import com.example.plana22.Models.Task
import com.example.plana22.Models.User
import com.example.plana22.R
import com.example.plana22.firebase.FireStoreClass
import com.example.plana22.utils.Constants
import com.example.plana22.utils.Constants.DOCUMENT_ID
import kotlinx.android.synthetic.main.activity_boards_list.*
import kotlinx.android.synthetic.main.activity_tasks_list.*

class TasksListActivity : BaseActivity() {
    lateinit var mBoardDetails : Board
    var boardDocumentId = ""
    lateinit var  mAssignedMembersDetailsList : ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks_list)

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this, boardDocumentId)
    }

    /**A function to get the result of Board Detail. and set it up as a recyclerview*/
    fun boardDetails(board: Board){
        mBoardDetails = board
         hideProgressDialog()
        setupActionBar()

        showProgressDialog((resources.getString(R.string.please_wait)))

        FireStoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)
    }

    /**A function to get assigned members detail list.*/
    fun boardMemberDetailsList(list: ArrayList<User>){

        mAssignedMembersDetailsList = list
        hideProgressDialog()
        //Here we are appending an item view for adding a list task list for the board.
        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)

        val adapter = TaskListItemsAdapter(this, mBoardDetails.taskList)
        rv_task_list.adapter = adapter

        rv_task_list.setHasFixedSize(true)
        rv_task_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    /**A function to get the result of add or updating the task list.*/
    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        // Show the progress dialog.
        showProgressDialog((resources.getString(R.string.please_wait)))
        // Here get the updated board details.
        FireStoreClass().getBoardDetails(this, mBoardDetails.documentId)
    }

    /**A function to get the task list name from the adapter class which we will be using to create a new task list in the database.*/
    fun createTaskList(taskListName: String){
        val task = Task(taskListName, FireStoreClass().getCurrentUserId())

        mBoardDetails.taskList.add(0, task) // Add task to the first position of ArrayList
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1) // Remove the last position as we have added the item manually for adding the TaskList.

        showProgressDialog((resources.getString(R.string.please_wait)))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    /**A function to delete the task list from database.*/
    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)  //Delete at the particular position

        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog((resources.getString(R.string.please_wait)))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails) //update the tasklist in F.Base
    }

    /*** A function to update the taskList*/
    fun updateTaskList(position: Int, listName: String, model: Task){
        val task = Task(listName,  model.createdBy )

        mBoardDetails.taskList[position] = task

        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog((resources.getString(R.string.please_wait)))
        FireStoreClass().addUpdateTaskList(this, mBoardDetails) //update the tasklist in F.Base

    }

    /**A function to create a card and update it in the task list.*/
    fun addCardToTaskList(position: Int, cardName: String) {
        // Remove the last item
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        //Initialising the assigned users list of the particular card
        val cardAssignedUserList : ArrayList<String> = ArrayList()
        cardAssignedUserList.add(FireStoreClass().getCurrentUserId())

        //setting up the card using the Card parameters
        val card = Card(cardName, FireStoreClass().getCurrentUserId(), cardAssignedUserList)

        //Adding the particular card already set up above to the card list in the TaskList
        val cardList = mBoardDetails.taskList[position].cards
        cardList.add(card)

        val task = Task(mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardList
        )

        mBoardDetails.taskList[position] = task

        showProgressDialog((resources.getString(R.string.please_wait)))
        FireStoreClass().addUpdateTaskList(this@TasksListActivity, mBoardDetails)
    //Reloading the whole tasklists

    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int){
        val intent = Intent(this, CardDetailsActivity::class.java)

        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails )
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskListPosition )
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition )
        intent.putExtra(Constants.BOARD_MEMBERS_LIST, mAssignedMembersDetailsList)

        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }

    /**Reloading the activity only if a change in the MemberActivity was made*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK &&
            requestCode == MEMBERS_REQUEST_CODE || requestCode == CARD_DETAILS_REQUEST_CODE ){
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardDetails(this, boardDocumentId)

        } else{
            Log.e("Cancelled", "Cancelled")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**A function to setup action bar*/
    private fun setupActionBar() {
        setSupportActionBar(toolbar_task_list_activity)

        val actionBar = supportActionBar

        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

            actionBar.title = mBoardDetails.name
        }

        //Making the "back" able to take the activity back to the previous activity
        toolbar_task_list_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    //Creates an options menu for linking to the Members activity when it is clicked
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when(item.itemId){
            R.id.action_members -> {
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateCardsInTaskList(taskListPosition: Int, cards: ArrayList<Card>){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        //initialising the new list ant it position to the Arraylist of cards in the database
        mBoardDetails.taskList[taskListPosition].cards = cards

        showProgressDialog((resources.getString(R.string.please_wait)))
        FireStoreClass().addUpdateTaskList(this@TasksListActivity, mBoardDetails)
    }

    companion object{
        const val MEMBERS_REQUEST_CODE : Int = 13
        const val CARD_DETAILS_REQUEST_CODE : Int = 14
    }
}












