package com.example.plana22
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.plana22.Activities.introduction.ProfileActivity
import com.example.plana22.Models.User
import com.example.plana22.firebase.FireStoreClass
import com.example.plana22.fragments.OverviewFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_task_dialog.*
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.logout_dialog.*
import kotlinx.android.synthetic.main.nav_activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header_drawer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    private val overviewFragment = OverviewFragment()
    companion object{
        const val MY_PROFILE_REQUEST_CODE : Int = 11  //Initialising the request code for the use data
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val overviewFragment = OverviewFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.flFragment, overviewFragment)
                .commit()
        }

        setContentView(R.layout.nav_activity_main)
        setSupportActionBar(mainView_toolbar)

        //application.Context = applicationContext

        val actionbar = supportActionBar

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0F //To remove the shadow beneath the activity toolbar

        FireStoreClass().loadUserData(this@MainActivity)

        /**Functionality to configure the drawer layout and Navigation view*/
        val drawerLayout: DrawerLayout = findViewById(R.id.nav_drawer_layout)
        //val drawerLayout : DrawerLayout = binding.R.id.nav_drawer_layout
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        /**OnclickListener for the hamburger menu*/
        navView.setNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.nav_home -> {
                    onBackPressed()
                    Toast.makeText(
                        applicationContext,
                        "Clicked Home", Toast.LENGTH_SHORT
                    ).show()
                }

                //Using startActivityForResult so it can get the user data into the profile activity
                R.id.nav_profile -> {
                    startActivityForResult(
                        Intent(
                            this,
                            ProfileActivity::class.java
                        ), MY_PROFILE_REQUEST_CODE
                    )
                    //overviewFragment.startSecondActivity(this)

                }

                R.id.nav_settings -> Toast.makeText(
                    applicationContext,
                    "Clicked Settings", Toast.LENGTH_SHORT
                ).show()

                R.id.nav_signOut ->
                    logOutDialog()  //Calling the log out dialog function

                R.id.nav_share -> Toast.makeText(
                    applicationContext,
                    "Clicked Share", Toast.LENGTH_SHORT
                ).show()

                R.id.nav_rate -> Toast.makeText(
                    applicationContext,
                    "Clicked Rate", Toast.LENGTH_SHORT
                ).show()
            }

            true
        }

    }

    /** This gets the result into the Activity if there is a present user
     * It loads and displays the data of the said user .*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode,data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){

            FireStoreClass().loadUserData(this)
            //FireStoreClass().loadUserDataInFragment(overviewFragment)

           // overviewFragment.onActivityResult(Activity.RESULT_OK, MY_PROFILE_REQUEST_CODE, data)
            //v overviewFragment.updateUserDataInOverview()
        }else{
            Log.e("Cancelled", "On activity result Cancelled")
        }
    }

    private fun activityStart(){

    }

    /**method to make the hamburger button responsive when clicked*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**A function to get the current user details from firebase.*/
    fun updateNavigationUserData(user: User){
        Glide
            .with(this@MainActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_nav_drawer_image)

        (user.firstName + " " + user.lastName).also { tv_nav_drawer_name.text = it }

        tv_nav_drawer_email.text = user.email
    }


    /**Method to prevent closing of the activity if the drawer is open and back is pressed*/
    override fun onBackPressed() {
        if (nav_drawer_layout.isDrawerOpen(GravityCompat.START)){
            nav_drawer_layout.closeDrawer(GravityCompat.START)
        } else{
            super.onBackPressed()
        }
    }

    /**Dialog to carry out the Log out function*/
    private fun logOutDialog() {
        val addDialog = Dialog(this, R.style.Logout_Theme_Dialog)
        addDialog.setContentView(R.layout.logout_dialog)
        addDialog.show()
        addDialog.setCancelable(false)  //to prevent dismissing the dialog when outside of it clicked

        /**Event listener for Add Task button*/
        addDialog.tvLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(
                applicationContext,
                "Logged out successfully", Toast.LENGTH_LONG
            ).show()
            addDialog.dismiss()  //dismiss the dialog

            finish()
        }

        /**Event listener for Cancel button*/
        addDialog.tvCancelLogout.setOnClickListener {
            addDialog.dismiss() //dismiss the dialog
        }
    }



}