package com.example.plana22.Activities.operations

import adapters.MembersListItemsAdapter
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plana22.Activities.introduction.BaseActivity
import com.example.plana22.Models.Board
import com.example.plana22.Models.User
import com.example.plana22.R
import com.example.plana22.firebase.FireStoreClass
import com.example.plana22.utils.Constants
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.dialog_search_member.*

class MembersActivity : BaseActivity() {
    private lateinit var mBoardDetails : Board
    private lateinit var mAssignedMembersList : ArrayList<User>
    lateinit var mSearchMemberDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        showProgressDialog(resources.getString(R.string.please_wait))

        setupActionBar()

        FireStoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)
    }


    /**A function to setup assigned members list into recyclerview.*/
    fun setupMembersList(list: ArrayList<User>){
        mAssignedMembersList = list
        hideProgressDialog()
        setupActionBar()

        val adapter = MembersListItemsAdapter(this, list)
        rv_members_list.adapter = adapter
        rv_members_list.layoutManager = LinearLayoutManager(this)
        rv_members_list.setHasFixedSize(true)
    }

    /**This adds the searched user to the 'AssignedTo' array list  of Board*/
    fun memberDetails(user: User){
        mBoardDetails.assignedTo.add(user.id)

        FireStoreClass().assignMemberToBoard(this, mBoardDetails, user)
    }

    /**A function to get the result of assigning the members.*/
    fun memberAssignSuccess(user: User){
        hideProgressDialog()

        mAssignedMembersList.add(user) //Adds the newly added member to the list of users in members recycler view

        dialogSearchMemberDismiss()
        setupMembersList(mAssignedMembersList) //reloads the recycler view. Using the new data

    }

    /**Setting the layout of the add member icon*/
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**On click listener for when the add member icon is clicked*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member ->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**Dialog for the input and adding of a new member*/
    private fun dialogSearchMember(){
        mSearchMemberDialog = Dialog(this)
        mSearchMemberDialog.setContentView(R.layout.dialog_search_member)
        mSearchMemberDialog.setCancelable(false)

        mSearchMemberDialog.tv_add.setOnClickListener {
            val email = mSearchMemberDialog.et_email_search_member.text.toString()
            if (email.isNotEmpty()) {
                showProgressDialog(resources.getString(R.string.please_wait))
                FireStoreClass().getMemberDetails(this, email)

            } else {
                Toast.makeText(
                    this,
                    "Please enter a member email address", Toast.LENGTH_SHORT
                ).show()
            }
        }

        mSearchMemberDialog.tv_cancel.setOnClickListener {
            mSearchMemberDialog.dismiss()
        }

        mSearchMemberDialog.show()
    }

    fun dialogSearchMemberDismiss(){
        mSearchMemberDialog.dismiss()
    }



    private fun setupActionBar(){
        setSupportActionBar(toolbar_members_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            //actionBar.title = "${mBoardDetails.name} ${ R.string.members}"

            actionBar.title = "${mBoardDetails.name } ${resources.getString(R.string.members)}"

        }

        //Making the "back" able to take the activity back to the previous activity
        toolbar_members_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

}