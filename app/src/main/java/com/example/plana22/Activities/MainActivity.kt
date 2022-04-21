package com.example.plana22.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.plana22.R
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        btn_getStarted.setOnClickListener {
            val intent = Intent(this, OverviewActivity::class.java)
            startActivity(intent)
        }

        middle_yellow_image.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)

            startActivity(intent)
        }

        val intent = Intent(this, OverviewActivity::class.java)
        startActivity(intent)
    }

}