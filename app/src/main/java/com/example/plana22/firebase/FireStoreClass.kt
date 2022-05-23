package com.example.plana22.firebase

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.plana22.Activities.introduction.ProfileActivity
import com.example.plana22.Activities.introduction.SignUpActivity
import com.example.plana22.Activities.operations.BoardsListActivity
import com.example.plana22.Activities.operations.CreateBoardActivity
import com.example.plana22.Activities.operations.MembersActivity
import com.example.plana22.Activities.operations.TasksListActivity
import com.example.plana22.MainActivity
import com.example.plana22.Models.Board
import com.example.plana22.Models.User
import com.example.plana22.fragments.OverviewFragment
import com.example.plana22.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()
    val overviewFragment = OverviewFragment()

    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }


    /**Method that handles the calling of all the data of a user that's in the database
     *  when he signs in or updates his profile*/
    fun loadUserData(activity: Activity, readBoardsList: Boolean = false){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())//gets the database document of the user that's signed in by using their UIDs which is gotten from the authentication side
            .get()  //gets the code
            .addOnSuccessListener {document ->//This runs a code of our wish if the login is successful
                val loggedInUser = document.toObject(User::class.java)
                if(loggedInUser != null){
                    when(activity){
                        is SignUpActivity ->
                            activity.signInUserSuccess(loggedInUser)

                        is MainActivity ->
                            activity.updateNavigationUserData(loggedInUser)

                        is ProfileActivity ->
                            activity.setDataIntoUI(loggedInUser)

                        is BoardsListActivity ->
                            activity.getUserFirstNameAndBoardList(loggedInUser, readBoardsList)



                    }
                }
            }.addOnFailureListener{ //This runs a code of our wish if the login fails
                    e ->
                Log.e("SignInUser", "Error writing document", e)
            }
    }


    /**A function to update the user profile data into the database.*/
    fun updateUserProfileData(activity: ProfileActivity, userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap).addOnCompleteListener {

                activity.profileUpdateSuccess()

            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, "Error while creating a board", e)
                Toast.makeText(
                    activity, "Error while updating the profile!",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    /**Method that handles the reading & constant updating in the Fragment UI of all the data of a user that's in the database when he signs in or an update is done */
    fun readDatabaseAndLoadUserDataToFragment(fragment: Fragment){
        val docRef = mFireStore.collection(Constants.USERS).document(getCurrentUserId())
        docRef.addSnapshotListener { snapshot, e ->
            val loggedInUser = snapshot?.toObject(User::class.java)

            when(fragment) {
                is OverviewFragment -> {

                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data here: ${snapshot.data}")
                        Log.d(TAG, "Current data first name: ${loggedInUser?.firstName}")
                        fragment.updateUserDataInOverview(loggedInUser!!)
                    } else {
                        Log.d(TAG, "Current data: null")
                    }
                }
            }
        }
    }


    /** A function for creating a board and making an entry in the database.*/
    fun createBoard(activity: CreateBoardActivity, board: Board) {
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge()) //This sets & merges all the user Info that's passed
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board created successfully")
                Toast.makeText(activity, "Board created successfully",
                    Toast.LENGTH_SHORT).show()

                activity.boardCreatedSuccessfully()
            }.addOnFailureListener {
                    exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                    "Error while creating a board",
                    exception)
            }
    }

    /**A method to get the board list that is assigned to the current user from the database using the UUID*/
    fun getBoardsList(activity: BoardsListActivity){
        mFireStore.collection(Constants.BOARDS)
            // A where array query is used as we want the list of the board in which the user is assigned. So here you can pass the current user id.
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId())
            .get()  // Will get the documents snapshots.
            .addOnSuccessListener { document->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Here a new instance for the Boards ArrayList is created.
                val boardList : ArrayList<Board> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)

                    board!!.documentId = i.id

                    boardList.add(board)
                }
                activity.populateListToUI(boardList)
            }.addOnFailureListener {

            }
    }


    /**A method to get the board details from the database using the id(documentId) to decide the particular one*/
    fun getBoardDetails(activity: TasksListActivity, documentId: String){
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)  //Checking which  board has the current Id
            .get()
            .addOnSuccessListener { document ->
                //This document is a snapshot that contains only the boards that has this particular i
                Log.i(activity.javaClass.simpleName, document.toString())

                //Calling the board details method from the TaskListActivity
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                activity.hideProgressDialog()

                // Send the result of board to the base activity.
                activity.boardDetails(board)

            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board", e)

            }
    }

    /**A function to get the list of user details which is assigned to the board.*/
    fun getAssignedMembersListDetails(activity: MembersActivity, assignedTo: ArrayList<String>) {
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo) // Here the database field name and the id's of the members.
            .get().addOnSuccessListener {
                    document  ->
                Log.i(activity.javaClass.simpleName, document.toString())
                // Convert all the document snapshot to the object using the User data model class.
                val userList : ArrayList<User> = ArrayList() //Creating an empty array list for the members

                // Convert all the document snapshot to the object using the User data model class.
                for(i in document.documents){
                    val user = i.toObject(User::class.java)!!

                    userList.add(user) //adding the members to the previously created arrayList
                }
                activity.setupMembersList(userList)
            }.addOnFailureListener {
                    e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board", e)
            }
    }

    /**A function to get the user details from Firestore Database using the email address.*/
    fun getMemberDetails(activity: MembersActivity, email: String) {
        // Here we pass the collection name from which we want the data.
        mFireStore.collection(Constants.USERS)
            // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
            .whereEqualTo(Constants.EMAIL, email)
            .get().addOnSuccessListener {
                    document ->
                //Conditional that checks if the typed email address is found in the user list
                if(document.documents.size > 0 ){
                    // Here call a function of base activity for transferring the result to it.
                    val user = document.documents[0].toObject(User::class.java)!!

                    activity.memberDetails(user)
                }else{
                    activity.hideProgressDialog()
                    activity.showSnackBar("No such member found")
                }

            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting the searched user ", e)
            }
    }

    /** A function to assign the newly searched member to the board and also update thee entire members list.*/
    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User) {
        val assignedMembersHashMap = HashMap<String, Any>()
        assignedMembersHashMap[Constants.ASSIGNED_TO] =  board.assignedTo

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedMembersHashMap)
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, "Board member added successfully")

                activity.memberAssignSuccess(user)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while adding board member", e)
            }

    }

    /**A function to create a task list in the board detail.*/
    fun addUpdateTaskList(activity: TasksListActivity, board: Board) {
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Task list updated successfully")

                activity.addUpdateTaskListSuccess()

            }.addOnFailureListener {
                    exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board", exception)

            }

    }


    /**Function to to get the user current  Id
     * AUTOLOGIN PROCESS
     * This also checks if there is an already set UID so it can go straight to the main activity page. And if there isn't, it just goes to the Intro activity for either sign in or sign up*/
    fun getCurrentUserId(): String {
        val currentUser =  FirebaseAuth.getInstance().currentUser
        var currentUserId = ""

        if (currentUser != null){
            currentUserId = currentUser.uid
        }
        return currentUserId
    }




}