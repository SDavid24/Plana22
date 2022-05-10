package com.example.plana22.Activities.introduction

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.plana22.R
import com.example.plana22.firebase.FireStoreClass
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //This hides the status bar and makes the activity full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setActionBar()

    }

    //Function that configures the action bar
    fun setActionBar(){
        setSupportActionBar(toolbar_sign_in_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }


        //Making the "back" able to take the activity back to the previous activity
        toolbar_sign_in_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        //This calls the register user method when the sign up button is clicked on
        btn_sign_in.setOnClickListener {
            signInUser()
        }
    }


    /**Method that gets the form filled from the validateForm() and uses the form to sign up
     *  a user using Firebase*/
    fun signInUser(){
        //trim function is to delete any empty space if the user mistakenly leave one after he fills his details
        val email : String = et_email_sign_in.text.toString().trim{it <= ' '}
        val password : String = et_password_sign_in.text.toString().trim{it <= ' '}

        if(validateSignInForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))   //First shows loading sign
            //How to sign in the user account using email and password
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    task ->
                    hideProgressDialog()

                    if (task.isSuccessful) {

                        FireStoreClass().loadUserData(this)

                    }else{
                        Toast.makeText(this,
                            "Failed to login account", Toast.LENGTH_LONG ).show()
                    }
            }
        }

    }


    /**Method to get the details from the sign in form and also a conditional to check if
     *  none is blank*/
    fun validateSignInForm(email: String, password: String) : Boolean{
        return when {

            TextUtils.isEmpty(email) ->{
                showSnackBar("Please fill in your Email Id")
                false
            }
            TextUtils.isEmpty(password) ->{
                showSnackBar("Please fill in your password")
                false
            }

            else -> {
                true
            }
        }

    }
}