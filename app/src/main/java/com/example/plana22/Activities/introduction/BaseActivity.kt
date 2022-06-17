package com.example.plana22.Activities.introduction

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.plana22.MainActivity
import com.example.plana22.Models.User
import com.example.plana22.R
import com.example.plana22.fragments.OverviewFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    lateinit var mProgressDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    /**Method to show progress dialog that something is loading*/
    fun showProgressDialog(text: String){
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.tv_progress_text.text = text

        mProgressDialog.show()
    }

    //Method to dismiss dialog
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    /**Method for the snack bar that'll display throughout the app*/
    fun showSnackBar(message: String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)

        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackBar_error_color))

        snackBar.show()
    }

    /**Method that handles the immediate afterwards of the sign up process*/
    fun userRegisteredSuccess(){
        Toast.makeText(this, "You have successfully registered",
            Toast.LENGTH_LONG).show()
        hideProgressDialog()


        //FirebaseAuth.getInstance().signInWithEmailAndPassword()
        //finish()
    }

    /**Method that handles the immediate afterwards of the sign in process*/
    fun signInUserSuccess(user: User) {
        Toast.makeText(this, "Welcome!", Toast.LENGTH_LONG).show()
        hideProgressDialog()

        Log.e("Location", "It reaches here")
        startActivity(Intent(this, MainActivity::class.java))

        finish()
    }


    /**Method to get the current user Id from Firebase*/
    fun getCurrentCurrentUserID() : String{

        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    /**Method that configures the back button in the app.
     * Ensures that the user doesn't close the app if the back button is clicked on
     * If it is clicked once, it goes back
     * If twice , it leaves the app*/
    fun doubleBackToExit(){
        if(doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_LONG
        ).show()

        Handler().postDelayed({doubleBackToExitPressedOnce = false }, 2500)
    }


}