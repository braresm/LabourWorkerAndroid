package com.example.dennisapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.dennisapp.entities.Shift
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import java.text.SimpleDateFormat
import java.util.*


class AddShiftActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_shift)

        // Get the ActionBar
        val actionBar: ActionBar? = supportActionBar

        // Change the ActionBar title
        actionBar?.title = "Add shift"

        // Show the back button in the ActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val etStartDate = findViewById<EditText>(R.id.et_start_date);
        val etEndDate = findViewById<EditText>(R.id.et_end_date);

        val etStartTime = findViewById<EditText>(R.id.et_start_time);
        val etEndTime = findViewById<EditText>(R.id.et_end_time);

        etStartDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    etStartDate.setText(dat)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        etEndDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    etEndDate.setText(dat)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        etStartTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                etStartTime.setText(SimpleDateFormat("HH:mm").format(calendar.time).toString())
            }

            TimePickerDialog(
                this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        etEndTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                etEndTime.setText(SimpleDateFormat("HH:mm").format(calendar.time).toString())
            }

            TimePickerDialog(
                this,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
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

    fun addShift(view: View) {
        firestore = FirebaseFirestore.getInstance()

        val etStartDate = findViewById<EditText>(R.id.et_start_date);
        val etEndDate = findViewById<EditText>(R.id.et_end_date);
        val etStartTime = findViewById<EditText>(R.id.et_start_time);
        val etEndTime = findViewById<EditText>(R.id.et_end_time);

        // Validate the shift data
        etStartDate.error = null
        etEndDate.error = null
        etStartTime.error = null
        etEndTime.error = null

        if (etStartDate.length() == 0) {
            etStartDate.error = "This field is required"
            return
        }
        if (etEndDate.length() == 0) {
            etEndDate.error = "This field is required"
            return
        }
        if (etStartTime.length() == 0) {
            etStartTime.error = "This field is required"
            return
        }
        if (etEndTime.length() == 0) {
            etEndTime.error = "This field is required"
            return
        }

        val sdf = SimpleDateFormat("dd-MM-yyyy")

        val startDate = sdf.parse(etStartDate.text.toString())
        val endDate = sdf.parse(etEndDate.text.toString())

        if (startDate > endDate) {
            etStartDate.error = "Start date must be before end date"
            return
        }

        // Save the shift data
        saveShiftData(startDate, endDate, etStartTime.text.toString(), etEndTime.text.toString())
    }

    private fun saveShiftData(startDate: Date, endDate: Date, startTime: String, endTime: String) {
        // Create the batch for writing the shift objects
        val batch = firestore.batch()

        // Iterate from start date to end date and create shift documents
        val shiftDate = Calendar.getInstance()
        shiftDate.time = startDate

        while (shiftDate.time <= endDate) {
            val docRef = firestore.collection("shifts").document()
            batch.set(
                docRef, hashMapOf(
                    "shiftDate" to shiftDate.time,
                    "startTime" to startTime,
                    "endTime" to endTime
                )
            )

            // Go to the next day
            shiftDate.add(Calendar.DATE, 1);
        }

        // Commit the documents in the batch
        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(
                    applicationContext,
                    "Shift was saved successfully.",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext,
                    "Shift was not saved successfully. Please try again later",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("addShift", exception.stackTraceToString())
            }
    }
}