package com.example.plana22.Activities.operations

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.plana22.Activities.introduction.BaseActivity
import com.example.plana22.Models.Board
import com.example.plana22.R
import com.example.plana22.firebase.FireStoreClass
import com.example.plana22.utils.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_profile.*

class CreateBoardActivity : BaseActivity() {
    var mSelectedImageFileUri: Uri? = null
    var mBoardImageFileUri: String = " "
    private lateinit var mFirstName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)

        setupActionBar()


        if (intent.hasExtra(Constants.FIRSTNAME)) {
            mFirstName = intent.getStringExtra(Constants.FIRSTNAME).toString()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_create_board_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            actionBar.title = resources.getString(R.string.create_board_title)
        }

        //Making the "back" able to take the activity back to the previous activity
        toolbar_create_board_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        photoClickedOn()    //calling the photoClickedOn method
        createBoardOnClickButton()     //calling the updateProfile method
    }


    /**Adds the click event for iv_profile_user_image*/
    private fun photoClickedOn() {
        iv_create_board_image.setOnClickListener {
            //Conditional that che
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
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

        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
            .with(this)
            .load(mSelectedImageFileUri)
            .centerCrop()
            .placeholder(R.drawable.ic_board_place_holder)
            .into(iv_create_board_image)
    }

    /**A function to upload the selected user image to firebase cloud storage.*/
    private fun uploadBoardImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {
            //Configuration of the image path and also to have distinct Uri
            val sRef: StorageReference = FirebaseStorage.getInstance().reference
                .child(
                    "USER_IMAGE" + System.currentTimeMillis() + "."
                            + Constants.getFileExtension(this, mSelectedImageFileUri!!)
                )

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                // The image upload is success
                    taskSnapshot ->
                Log.i(
                    "Firebase Image URL", taskSnapshot.metadata!!
                        .reference!!.downloadUrl.toString()
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.i("Downloadable Image URL", uri.toString())

                    // assign the image url to the variable.
                    mBoardImageFileUri = uri.toString()

                    hideProgressDialog()

                    // Call a function to update user details in the database.
                    boardCreation()

                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    exception.message, Toast.LENGTH_LONG
                ).show()

                hideProgressDialog()
            }

        }

    }

    fun boardCreation() {
        // A list is created to add the assigned members.
        // This can be modified later on as of now the user itself will be the member of the board.
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentCurrentUserID())
        //assignedUsersArrayList.add(FireStoreClass().getCurrentUserId())

        val boardName = et_board_name.text.toString().trim { it <= ' ' }
        val documentId = " "
        val image = mBoardImageFileUri
        val createdBy = mFirstName //Constants.DOCUMENT_ID

        if (validateCreateBoardForm(boardName)) {
            val board = Board(
                documentId,
                boardName,
                image,
                createdBy,
                assignedUsersArrayList
            )

            showProgressDialog(resources.getString(R.string.please_wait))

            FireStoreClass().createBoard(this, board)
        }

    }

    fun createBoardOnClickButton() {
        btn_create_board.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                boardCreation()
            }
        }
    }


    /**Method to get the details from the create board form and also a conditional to check if
     *it is blank*/
    fun validateCreateBoardForm(boardName : String) : Boolean{
        return when {

            TextUtils.isEmpty(boardName) -> {
                showSnackBar("Please fill in a board title")
                false
            }
            else -> {
                true
            }
        }

    }


    /**Aftermath of the successful creation of a board*/
    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)  //Setting the Result_Ok value when the profile updates

        finish()
    }

}