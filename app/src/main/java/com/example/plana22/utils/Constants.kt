package com.example.plana22.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult

object Constants {

    // Firebase Constants
    const val BOARDS = "Boards"
    const val USERS : String = "Users"
    const val TASK_LIST : String = "taskList"

    // Firebase database field names
    const val IMAGE: String= "image"
    const val FIRSTNAME: String= "firstName"
    const val LASTNAME: String= "lastName"
    const val MOBILE: String= "mobile"
    const val ASSIGNED_TO: String = "assignedTo"
    const val DOCUMENT_ID: String= "documentId"
    const val BOARD_DETAIL = "board_detail"
    const val EMAIL: String = "email"
    const val ID: String= "id"

    //Create a variable for GALLERY Selection which will be later used in the onActivityResult method
    const val READ_STORAGE_PERMISSION_CODE = 1
    // A unique code of image selection from Phone Storage.
    private const val PICK_IMAGE_REQUEST_CODE = 2

    const val CREATE_BOARD_REQUEST_CODE = 10


    /**A function for user profile image selection from phone storage*/
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }


    /**A function to get the extension of selected image.*/
    fun getFileExtension(activity: Activity, uri: Uri) : String?{
        return  MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }

}