package com.example.plana22.Activities.introduction

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.plana22.Models.User
import com.example.plana22.R
import com.example.plana22.firebase.FireStoreClass
import com.example.plana22.fragments.OverviewFragment
import com.example.plana22.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity() {
    var mSelectedImageFileUri : Uri? = null
    var mProfileImageFileUri : String = ""   //Initializing the downloadable URI
    var mUserDetails : User? = null

    var overviewFragment = OverviewFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupActionBar()

        FireStoreClass().loadUserData(this@ProfileActivity)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_my_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }

        //Making the "back" able to take the activity back to the previous activity
        toolbar_my_profile_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        //This calls the update profile  method when the profile button is clicked on
        btn_update.setOnClickListener {
            updateUserProfileDataInProfAct()
        }

        photoClickedOn()    //calling the photoClickedOn method
        updateProfile()     //calling the updateProfile method
    }

    /**Adds the click event for iv_profile_user_image*/
    private fun photoClickedOn(){
        iv_profile_user_image.setOnClickListener {
            //Conditional that check
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }
    }


    /** This function will identify the result of runtime permission after the user allows or deny
     * permission based on the unique code.*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            } else {
                Toast.makeText(
                    this,
                    "Oops you just denied the permission for storage. You can " +
                            "allow it in the settings", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //The uri of selected image from phone storage.
        mSelectedImageFileUri = data!!.data

        Glide
            .with(this@ProfileActivity)
            .load(mSelectedImageFileUri)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_profile_user_image)
    }

    /**A function to upload the selected user image to firebase cloud storage.*/
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if(mSelectedImageFileUri != null){
            //Configuration of the image path and also to have distinct Uri
            val sRef : StorageReference = FirebaseStorage.getInstance().reference
                .child("USER_IMAGE" + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(this, mSelectedImageFileUri!!)
                )

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                // The image upload is success
                    taskSnapshot ->
                Log.i( "Firebase Image URL", taskSnapshot.metadata!!
                    .reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    Log.i("Downloadable Image URL", uri.toString())

                    // assign the image url to the variable.
                    mProfileImageFileUri = uri.toString()

                    hideProgressDialog()

                    // Call a function to update user details in the database.
                    updateUserProfileDataInProfAct()

                }
            }.addOnFailureListener{
                    exception -> Toast.makeText(this@ProfileActivity,
                exception.message, Toast.LENGTH_LONG).show()

                hideProgressDialog()
            }

        }

    }

    /**A function to update the user profile details into the database.*/
    private fun updateUserProfileDataInProfAct(){
        val userHashMap = HashMap<String, Any>()
        var anyChanges = false

        //Conditional to check if the present image is the same with the one already in the database
        if(mProfileImageFileUri.isNotEmpty()
            && mProfileImageFileUri != mUserDetails!!.image){
            userHashMap[Constants.IMAGE] = mProfileImageFileUri
            anyChanges = true
        }
        //Conditional to check if the present name is the same with the one already in the database
        if (et_firstname_profile.text.toString() != mUserDetails!!.firstName){
            userHashMap[Constants.FIRSTNAME] = et_firstname_profile.text.toString()
            anyChanges = true
        }
        //Conditional to check if the present name is the same with the one already in the database
        if (et_lastname_profile.text.toString() != mUserDetails!!.lastName){
            userHashMap[Constants.LASTNAME] = et_lastname_profile.text.toString()
            anyChanges = true
        }
        //Conditional to check if the present mobile is the same with the one already in the database
        if (et_mobile_profile.text.toString() != mUserDetails!!.mobile.toString()){
            userHashMap[Constants.MOBILE] = et_mobile_profile.text.toString().toLong()
            anyChanges = true
        }
        //Conditional to check if any change was made at all
        if(anyChanges){
            FireStoreClass().updateUserProfileData(this, userHashMap)
            //FireStoreClass().readDatabase(overviewFragment)

        }else{
            Toast.makeText(
                this,
                "No change was made.", Toast.LENGTH_LONG
            ).show()
        }

    }

    /**Click listener for the update button*/
    private fun updateProfile(){
        btn_update.setOnClickListener {

            if(mSelectedImageFileUri != null){
                uploadUserImage()
                //FireStoreClass().loadUserDataInFragment(overviewFragment)
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))

                updateUserProfileDataInProfAct()

            }
        }
    }

    /**Method that handles the immediate afterwards of the Profile update process*/
    fun profileUpdateSuccess(){
        hideProgressDialog()
        Toast.makeText(this, "You have successfully updated your profile",
            Toast.LENGTH_LONG).show()

        setResult(Activity.RESULT_OK)  //Setting the Result_Ok value when the profile updates

        //overviewFragment.updateUserDataInOverview(mUserDetails!!)
        //OverviewFragment().startSecondActivity(this)
        //startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /**A function to set the existing details in UI.*/
    fun setDataIntoUI(user: User){
        // Initialize the user details variable
        mUserDetails = user

        Glide
            .with(this@ProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_profile_user_image)

        et_firstname_profile.setText(user.firstName)
        et_lastname_profile.setText(user.lastName)
        et_email_profile.setText(user.email)

        if(mUserDetails!!.mobile != 0L){
            et_mobile_profile.setText(user.mobile.toString())
        }
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}
