package com.example.plana22.Activities.operations

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.plana22.Activities.introduction.BaseActivity
import com.example.plana22.Adapters.BoardsListAdapter
import com.example.plana22.Models.Board
import com.example.plana22.Models.User
import com.example.plana22.R
import com.example.plana22.firebase.FireStoreClass
import com.example.plana22.utils.Constants
import kotlinx.android.synthetic.main.activity_boards_list.*
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_profile.*

class BoardsListActivity : BaseActivity() {
    lateinit var mFirstName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boards_list)

        setupActionBar()
        FireStoreClass().loadUserData(this@BoardsListActivity, true)

        fab_create_board.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.FIRSTNAME, mFirstName)
            startActivityForResult(intent, Constants.CREATE_BOARD_REQUEST_CODE)
        }
    }

    /**A function to populate the result of BOARDS list in the UI i.e in the recyclerView.*/
    fun populateListToUI(boardList: ArrayList<Board>){
        if(boardList.size > 0) {
            tv_no_boards_available.visibility = GONE
            rv_boards_list.visibility = View.VISIBLE
            val adapter = BoardsListAdapter(this, boardList)
            rv_boards_list.adapter = adapter

            rv_boards_list.setHasFixedSize(true)
            rv_boards_list.layoutManager = LinearLayoutManager(this)

            adapter.setOnClickListener(object : BoardsListAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@BoardsListActivity, TasksListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        }else{
            tv_no_boards_available.visibility = View.VISIBLE
            rv_boards_list.visibility = GONE

        }
    }


    /**A function to get the current user details from firebase.*/
    fun getUserFirstNameAndBoardList(user: User, readBoardsList: Boolean) {

        mFirstName = user.firstName

        //conditional that checks and only loads the boards list if readBoardsList is true
        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardsList(this)
            hideProgressDialog()
        }
    }

    /** This gets the result into the Activity if there is a present user
     * It loads and displays the data of the said user .*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

         if(resultCode == Activity.RESULT_OK && requestCode == Constants.CREATE_BOARD_REQUEST_CODE){
            FireStoreClass().getBoardsList(this)
        }
        else{
            Log.e("Cancelled", "On Activity result cancelled")
        }
    }

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_boards_list_activity)

        val actionBar = supportActionBar

        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

            actionBar.title = resources.getString(R.string.boards_list)
        }

        //Making the "back" able to take the activity back to the previous activity
        toolbar_boards_list_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }
}