package com.example.dennisapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray


class DictionaryActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        // Get the ActionBar
        val actionBar: ActionBar? = supportActionBar

        // Change the ActionBar title
        actionBar?.title = "Dictionary"

        // Show the back button in the ActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        progressBar = findViewById(R.id.pb_dictionary_response)

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

    fun getWordMeanings(view: View) {
        val wordInput = findViewById<EditText>(R.id.et_search_word)
        var definitions = ArrayList<String>()

        // Show the progress circle indicator
        this.progressBar.visibility = View.VISIBLE

        // Text entered in the input
        val word = wordInput.text.trim()

        val apiUrl = "${BuildConfig.DICTIONARY_API_URL}/$word"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, apiUrl,
            { response ->
                definitions = this.extractMeaningsFromResponse(response)
                this.buildListView(definitions)
                this.progressBar.visibility = View.GONE

                // hide the keyboard
                val inputMethodManager: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.applicationWindowToken, 0)
            },
            { error ->
                var errorMessage = "Error while calling the API"
                if (error.networkResponse.statusCode == 404) {
                    errorMessage = "No result found for $word"
                }
                Toast.makeText(
                    applicationContext,
                    errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
                this.buildListView(definitions)
                this.progressBar.visibility = View.GONE
            }
        )

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    private fun extractMeaningsFromResponse(response: String): ArrayList<String> {
        val results = ArrayList<String>();

        val responseObject = JSONArray(response)
        val item = responseObject.getJSONObject(0)
        val meanings = item.getJSONArray("meanings")

        for (meaning_id in 0 until meanings.length()) {
            val meaning = meanings.getJSONObject(meaning_id)
            val definitions = meaning.getJSONArray("definitions")

            for (definition_id in 0 until definitions.length()) {
                val definition = definitions.getJSONObject(definition_id)
                results.add(definition.getString("definition"))
            }
        }
        return results
    }

    private fun buildListView(meanings: List<String>) {
        val adapter: ArrayAdapter<*> = ArrayAdapter(
            this,
            R.layout.dictionary_listview, meanings
        )

        val listView: ListView = findViewById<View>(R.id.lw_dictionary_results) as ListView
        listView.adapter = adapter
    }
}