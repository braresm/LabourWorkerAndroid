package com.example.dennisapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.CalendarView.OnDateChangeListener
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.dennisapp.entities.Shift
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.text.SimpleDateFormat
import java.util.*


class CalendarActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore;

    private val sdf = SimpleDateFormat("dd-MM-yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        // Get the ActionBar
        val actionBar: ActionBar? = supportActionBar

        // Change the ActionBar title
        actionBar?.title = "Calendar"

        // Show the back button in the ActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Add action on floating button
        val btnAddShift = findViewById<FloatingActionButton>(R.id.btn_add_shift)
        btnAddShift.setOnClickListener(View.OnClickListener {
            val addShiftIntent = Intent(this@CalendarActivity, AddShiftActivity::class.java)
            startActivity(addShiftIntent)
        })

        val calendarView = findViewById<CalendarView>(R.id.cw_shifts_calendar)
        calendarView.setOnDateChangeListener(OnDateChangeListener { calendarView, year, month, day ->
            val calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.YEAR, year);

            // Get the shifts for the selected date
            getShiftsForDate(calendar.time)
        })
    }

    override fun onStart() {
        super.onStart()
        getShiftsForDate(Date())
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

    private fun getShiftsForDate(date: Date) {
        // Format the date by setting the time components to 0
        val shiftDate = Calendar.getInstance()
        shiftDate.time = date
        shiftDate.set(Calendar.HOUR_OF_DAY, 0)
        shiftDate.set(Calendar.MINUTE, 0)
        shiftDate.set(Calendar.SECOND, 0)
        shiftDate.set(Calendar.MILLISECOND, 0)

        firestore = FirebaseFirestore.getInstance()

        // Ge the shifts object of the given date
        var query = firestore.collection("shifts").whereEqualTo("shiftDate", shiftDate.time)

        val shiftTimes = ArrayList<String>();

        query.get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val shift = doc.toObject<Shift>()
                    shiftTimes.add("From ${shift.startTime} until ${shift.endTime}")
                }
                if (shiftTimes.isEmpty()) {
                    shiftTimes.add("You have no shifts configure on ${sdf.format(date)}.")
                }
                this.buildListView(shiftTimes)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext,
                    "Cannot retrieve shifts for the selected date",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("getShiftsForDate", exception.stackTraceToString())
            }
    }

    private fun buildListView(shiftTimes: List<String>) {
        val adapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.shifts_listview, shiftTimes
        )

        val listView: ListView = findViewById<View>(R.id.lw_shifts_results) as ListView
        listView.adapter = adapter
    }
}