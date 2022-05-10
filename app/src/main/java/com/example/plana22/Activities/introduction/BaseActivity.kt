package com.example.plana22.Activities.introduction

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
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

        FirebaseAuth.getInstance().signOut()
        finish()
    }

    /**Method that handles the immediate afterwards of the sign in process*/
    fun signInUserSuccess(user: User) {
        Toast.makeText(this, "Welcome!",
            Toast.LENGTH_LONG).show()
        hideProgressDialog()

        Log.e("Location", "It reaches here")
        startActivity(Intent(this, MainActivity::class.java))

        finish()
    }

}