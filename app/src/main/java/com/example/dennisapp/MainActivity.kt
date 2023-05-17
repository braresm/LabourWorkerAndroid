package com.example.dennisapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val userType = intent.getStringExtra("userType").toString()

        val iwHealth = findViewById<ImageView>(R.id.iw_health)
        val btnShowServices = findViewById<Button>(R.id.btnShowServices)
        val btnOpenDictionary = findViewById<Button>(R.id.btnOpenDictionary)
        val btnCallPhone = findViewById<Button>(R.id.btnCallPhone)
        val btnCalendar = findViewById<Button>(R.id.btnCalendar)

        iwHealth.setOnClickListener {
            val healthIntent = Intent(this, HealthActivity::class.java)
            startActivity(healthIntent)
        }

        btnShowServices.setOnClickListener {
            val servicesIntent = Intent(this, ServicesActivity::class.java)
            startActivity(servicesIntent)
        }

        btnOpenDictionary.setOnClickListener {
            val dictionaryIntent = Intent(this, DictionaryActivity::class.java)
            startActivity(dictionaryIntent)
        }

        btnCallPhone.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:${BuildConfig.CONTACT_PHONE_NUMBER}")
            startActivity(callIntent)
        }

        btnCalendar.setOnClickListener {
            val calendarIntent = Intent(this, CalendarActivity::class.java)
            startActivity(calendarIntent)
        }

        if("WORKER" == userType) {
            iwHealth.visibility = View.VISIBLE;
            btnShowServices.visibility = View.VISIBLE
            btnOpenDictionary.visibility = View.VISIBLE
            btnCalendar.visibility = View.VISIBLE
        }
        if("CLIENT" == userType) {
            btnShowServices.visibility = View.VISIBLE
            btnCallPhone.visibility = View.VISIBLE
        }
    }
}