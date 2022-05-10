package com.example.plana22.firebase

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.plana22.Activities.introduction.ProfileActivity
import com.example.plana22.Activities.introduction.SignInActivity
import com.example.plana22.Activities.introduction.SignUpActivity
import com.example.plana22.MainActivity
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

    }


    /**Method that handles the calling of all the data of a user that's in the database
     *  when he signs in or updates his profile*/
    fun loadUserData(activity: Activity){
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
                    }
                }
            }.addOnFailureListener{ //This runs a code of our wish if the login fails
                    e ->
                Log.e("SignInUser", "Error writing document", e)
            }
    }


    /**Method that handles the reading & constant updating in the Fragment UI of all the data of a user that's in the database when he signs in or an update is done */
    fun readDatabase(fragment: Fragment){
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