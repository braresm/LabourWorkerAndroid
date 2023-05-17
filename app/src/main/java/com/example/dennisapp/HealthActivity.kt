package com.example.dennisapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar

class HealthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health)

        // Get the ActionBar
        val actionBar: ActionBar? = supportActionBar

        // Change the ActionBar title
        actionBar?.title = "Health Rules";

        // Show the back button in the ActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true);
    }

    // This event will enable the back function when
    // back button from ActivityBar is pressed
    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}