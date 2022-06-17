package com.example.plana22.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.plana22.firebase.FireStoreClass
import com.example.plana22.Activities.introduction.IntroActivity
import com.example.plana22.MainActivity
import com.example.plana22.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //Calling 'em functions
        splashLoad()
    }


    //This method makes the Splash activity move to the Intro or main activity after the stipulated time
    private fun splashLoad(){

        Handler().postDelayed({
            val currentUserId = FireStoreClass().getCurrentUserId()

            if (currentUserId.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, IntroActivity::class.java))
            }
            finish()
        }, 2500
        )

    }

}