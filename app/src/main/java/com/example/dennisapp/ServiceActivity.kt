package com.example.dennisapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.dennisapp.entities.Service
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


class ServiceActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        // Get the ActionBar
        val actionBar: ActionBar? = supportActionBar

        // Change the ActionBar title
        actionBar?.title = "Service";

        // Show the back button in the ActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true);

        val documentId = intent.getStringExtra("serviceDocId")

        // Get the service details from Firestore
        if (documentId != null) {
            getServiceDetails(documentId)
        }
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

    private fun getServiceDetails(docId: String) {
        firestore = FirebaseFirestore.getInstance()
        var docRef = firestore.collection("services").document(docId)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Convert response to model
                    val service = document.toObject<Service>()

                    if (service != null) {
                        // Get the User Interface fields
                        val tvTitle = findViewById<TextView>(R.id.tv_service_title)
                        val tvPrice = findViewById<TextView>(R.id.tv_service_price)
                        val tvDescription = findViewById<TextView>(R.id.tv_service_description)
                        val btnServiceContact = findViewById<Button>(R.id.btn_service_contact)

                        // Set values in User Interface
                        tvTitle.text = service.name
                        tvPrice.text = "$${service.price}/hour"
                        tvDescription.text = service.description

                        // Add event listener for "Contact me" button
                        btnServiceContact.setOnClickListener {
                            val smsIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("sms:${BuildConfig.CONTACT_PHONE_NUMBER}")
                            )
                            smsIntent.putExtra(
                                "sms_body",
                                "${BuildConfig.SMS_TEMPLATE} ${service.name}"
                            );
                            startActivity(smsIntent);
                        }
                    }


                } else {
                    Toast.makeText(
                        applicationContext,
                        "This service does not exists",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext,
                    "Service details could not be retrieved",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("getServiceDetails", exception.stackTraceToString())
            }
    }

}