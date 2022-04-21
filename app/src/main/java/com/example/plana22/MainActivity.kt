package com.example.plana22
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_overview.*
import kotlinx.android.synthetic.main.activity_overview.overview_toolbar
import kotlinx.android.synthetic.main.nav_activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.nav_activity_main)
        setSupportActionBar(mainView_toolbar)

        val actionbar = supportActionBar

        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            // actionbar.title = "Add Task"
        }
        supportActionBar?.elevation = 0F //To remove the shadow beneath the activity toolbar


        /**Functionality to configure the drawer layout and Navigation view*/
        val drawerLayout : DrawerLayout = findViewById(R.id.nav_drawer_layout)
        //val drawerLayout : DrawerLayout = binding.R.id.nav_drawer_layout
        val navView : NavigationView = findViewById(R.id.nav_view)

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
                        "Clicked Home", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_settings -> Toast.makeText(
                    applicationContext,
                    "Clicked Settings", Toast.LENGTH_SHORT).show()
                R.id.nav_login -> Toast.makeText(
                    applicationContext,
                    "Clicked Login", Toast.LENGTH_SHORT).show()
                R.id.nav_share -> Toast.makeText(
                    applicationContext,
                    "Clicked Share", Toast.LENGTH_SHORT).show()
                R.id.nav_rate -> Toast.makeText(
                    applicationContext,
                    "Clicked Rate", Toast.LENGTH_SHORT).show()
            }

            true
        }
    }

    /**method to make the hamburger button responsive when clicked*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**Method to prevent closing of the activity if the drawer is open and back is pressed*/
    override fun onBackPressed() {
        if (nav_drawer_layout.isDrawerOpen(GravityCompat.START)){
            nav_drawer_layout.closeDrawer(GravityCompat.START)
        } else{
            super.onBackPressed()
        }
    }
}