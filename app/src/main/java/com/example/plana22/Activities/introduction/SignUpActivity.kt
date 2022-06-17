package com.example.plana22.Activities.introduction

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.plana22.Models.User
import com.example.plana22.R
import com.example.plana22.firebase.FireStoreClass
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //This hides the status bar and makes the activity full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setActionBar()   //calling the setupActionBar function
    }

    //Function that configures the action bar
    fun setActionBar(){
        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        //Making the "back" able to take the activity back to the previous activity
        toolbar_sign_up_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        //This calls the register user method when the sign up button is clicked on
        btn_sign_up.setOnClickListener {
            signUpUser()
        }
    }


    /**Method that gets the form filled from the validateForm() and uses the form to sign up a user using Firebase
     * For more details visit: https://firebase.google.com/docs/auth/android/custom-auth
     */
    private fun signUpUser(){
        val firstName : String = et_firstname_sign_up.text.toString().trim{it <= ' '}
        val lastName : String = et_lastname_sign_up.text.toString().trim{it <= ' '}
        val email : String = et_email_sign_up.text.toString().trim{it <= ' '}
        val password : String = et_password_sign_up.text.toString().trim{it <= ' '}

        if(validateSignUpForm(firstName, lastName, email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))

            //How to create the user account using email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                        task ->
                    if (task.isSuccessful){
                        hideProgressDialog()
                        val firebaseUser = task.result!!.user!!  //How to access the user
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, firstName, lastName, registeredEmail)

                        FireStoreClass().registerUser(this, user)
                    }else
                        Toast.makeText(this,
                            "Account creation failed", Toast.LENGTH_LONG ).show()
                }

        }
    }

    /**Method to get the details from the sign up form and also a conditional to check if
     *  none is blank*/
    fun validateSignUpForm(firstName : String, lastName: String,
                           email: String, password: String) : Boolean{
        return when {

            TextUtils.isEmpty(firstName) ->{
                showSnackBar("Please fill in your first name")
                false
            }
            TextUtils.isEmpty(lastName) ->{
                showSnackBar("Please fill in your last name")
                false
            }
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

    override fun onBackPressed() {
        doubleBackToExit()
    }


}