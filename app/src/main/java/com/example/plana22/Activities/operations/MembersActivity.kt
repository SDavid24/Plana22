package com.example.plana22.Activities.operations

import adapters.MembersListItemsAdapter
import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MembersActivity : BaseActivity() {
    private lateinit var mBoardDetails : Board
    private lateinit var mAssignedMembersList : ArrayList<User>
    lateinit var mSearchMemberDialog : Dialog
    private var anyChangesMade : Boolean = false


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

    override fun onBackPressed() {
        if(anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()

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

        anyChangesMade = true

        dialogSearchMemberDismiss()
        setupMembersList(mAssignedMembersList) //reloads the recycler view. Using the new data


        SendNotificationToUserAsyncTAsk(mBoardDetails.name, user.fcmToken)
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


    /**This Creates a AsyncTask class for sending the notification to user based on the FCM Token.)*/
    private inner class SendNotificationToUserAsyncTAsk(val boardName: String, val token: String)
        : AsyncTask<Any, Void, String>() {

        /**
         * This function is for the task which we wants to perform before background execution.
         * Here we have shown the progress dialog to user that UI is not frozen but executing something in background.
         */
        override fun onPreExecute() {
            super.onPreExecute()

            showProgressDialog(resources.getString(R.string.please_wait))
        }

        /** This function will be used to perform background execution.*/
        override fun doInBackground(vararg p0: Any?): String {
            var result : String
            var connection : HttpURLConnection? = null

            try {
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.doInput = true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"  //Set the method for the URL request, one of: POST

                /**
                 * Sets the general request property. If a property with the key already
                 * exists, overwrite its value with the new value.
                 */
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                // Adds the firebase Server Key.
                connection.setRequestProperty(
                    Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
                )

                connection.useCaches = false

                /**
                 * Creates a new data output stream to write data to the specified
                 * underlying output stream. The counter written is set to zero.
                 */
                val wr = DataOutputStream(connection.outputStream)

                //Create a notification data payload.)
                //START

                //Creates JSONObject Request
                val jsonRequest = JSONObject()
                // Create a data object
                val dataObject = JSONObject()

                // Here you can pass the title as per requirement as here we have added some text and board name.
                dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the Board $boardName")
                // Here you can pass the message as per requirement as here we have added some text and appended the name of the Board Admin.
                dataObject.put(
                    Constants.FCM_KEY_MESSAGE,
                    "You have been assigned to the new board by " +
                            mAssignedMembersList[0].firstName
                )

                // Here add the data object and the user's token in the jsonRequest object.
                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)
                // END

                /**
                 * Writes out the string to the underlying output stream as a
                 * sequence of bytes. Each character in the string is written out, in
                 * sequence, by discarding its high eight bits. If no exception is
                 * thrown, the counter written is incremented by the
                 * length of s.
                 */
                wr.writeBytes(jsonRequest.toString())
                wr.flush() // Flushes this data output stream.
                wr.close() // Closes this output stream and releases any system resources associated with the stream

                val httpResult: Int =
                    connection.responseCode // Gets the status code from an HTTP response message.

                if (httpResult == HttpURLConnection.HTTP_OK) {

                    /**
                     * Returns an input stream that reads from this open connection.
                     */
                    val inputStream = connection.inputStream

                    /**
                     * Creates a buffering character-input stream that uses a default-sized input buffer.
                     */
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line: String?
                    try {
                        /**
                         * Reads a line of text.  A line is considered to be terminated by any one
                         * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
                         * followed immediately by a linefeed.
                         */
                        while (reader.readLine().also { line = it } != null) {
                            sb.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            /**
                             * Closes this input stream and releases any system resources associated
                             * with the stream.
                             */
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()
                } else {
                    /**
                     * Gets the HTTP response message, if any, returned along with the
                     * response code from a server.
                     */
                    result = connection.responseMessage
                }

            } catch (e: SocketTimeoutException) {
                result = "Connection Timeout"
            } catch (e: Exception) {
                result = "Error : " + e.message
            } finally {
                connection?.disconnect()
            }


            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            hideProgressDialog()

            // JSON result is printed in the log.
            Log.e("JSON Response Result", result!!)
        }
    }

}