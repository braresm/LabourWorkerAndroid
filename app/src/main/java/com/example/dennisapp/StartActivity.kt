package com.example.dennisapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val btnWorker = findViewById<Button>(R.id.btnWorker)
        val btnClient = findViewById<Button>(R.id.btnClient)

        btnWorker.setOnClickListener {
            val mainActivity = Intent(this, MainActivity::class.java)
            mainActivity.putExtra("userType", "WORKER")
            startActivity(mainActivity)
        }

        btnClient.setOnClickListener {
            val mainActivity = Intent(this, MainActivity::class.java)
            mainActivity.putExtra("userType", "CLIENT")
            startActivity(mainActivity)
        }
    }
}