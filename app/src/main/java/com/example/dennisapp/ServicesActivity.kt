package com.example.dennisapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dennisapp.entities.Service
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class ServicesActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)

        // Get the ActionBar
        val actionBar: ActionBar? = supportActionBar

        // Change the ActionBar title
        actionBar?.title = "Services";

        // Show the back button in the ActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true);

        // Get the available services from Firestore
        getAvailableServices()
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

    private fun getAvailableServices() {
        firestore = FirebaseFirestore.getInstance()
        var query = firestore.collection("services")
        var options =
            FirestoreRecyclerOptions.Builder<Service>().setQuery(query, Service::class.java)
                .setLifecycleOwner(this).build()

        val adapter = object : FirestoreRecyclerAdapter<Service, UserViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                val view = LayoutInflater.from(this@ServicesActivity)
                    .inflate(R.layout.services_listview, parent, false)
                return UserViewHolder(view)
            }

            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: Service) {
                // Firestore service document id
                val documentId = snapshots.getSnapshot(position).id

                // Get the User Interface fields
                val tvPrice: TextView = holder.itemView.findViewById(R.id.tv_service_item_price)
                val tvTitle: TextView = holder.itemView.findViewById(R.id.tv_service_item_title)
                val tvShortDescription: TextView =
                    holder.itemView.findViewById(R.id.tv_service_item_short_description)

                // Set values in User Interface
                tvPrice.text = "$${model.price.toString()}/hour"
                tvTitle.text = model.name
                tvShortDescription.text = model.shortDescription

                // Set the item background color
                holder.itemView.setBackgroundColor(Color.parseColor("#${model.backgroundColor}"))

                // Add event listener for each item
                holder.itemView.setOnClickListener(View.OnClickListener {
                    val serviceIntent = Intent(this@ServicesActivity, ServiceActivity::class.java)
                    serviceIntent.putExtra("serviceDocId", documentId);
                    startActivity(serviceIntent)
                })
            }

        }
        val rvServices = findViewById<View>(R.id.rvServices) as RecyclerView
        rvServices.adapter = adapter
        rvServices.layoutManager = LinearLayoutManager(this)
        rvServices.itemAnimator = null
    }
}