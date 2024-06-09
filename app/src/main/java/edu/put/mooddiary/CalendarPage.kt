package edu.put.mooddiary

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CalendarPage : AppCompatActivity() {

    private lateinit var datePicker: DatePicker
    private lateinit var markSpinner: Spinner
    private lateinit var markImageView: ImageView
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var backButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val marks = arrayOf("-", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    private val markImages = arrayOf(
        null, // No image for "-"
        R.drawable.em1, R.drawable.em2, R.drawable.em3,
        R.drawable.em4, R.drawable.em5, R.drawable.em6,
        R.drawable.em7, R.drawable.em8, R.drawable.em9
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_page)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        datePicker = findViewById(R.id.datePicker)
        markSpinner = findViewById(R.id.markSpinner)
        markImageView = findViewById(R.id.markImageView)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        saveButton = findViewById(R.id.saveButton)
        backButton = findViewById(R.id.backButton)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, marks)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        markSpinner.adapter = adapter

        markSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                val selectedImage = markImages[position]
                if (selectedImage != null) {
                    markImageView.setImageResource(selectedImage)
                } else {
                    markImageView.setImageDrawable(null) // No image for "-"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                markImageView.setImageDrawable(null) // No image for "-"
            }
        }

        saveButton.setOnClickListener {
            saveData()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            loadData(year, monthOfYear, dayOfMonth)
        }

        // Load initial data
        loadData(datePicker.year, datePicker.month, datePicker.dayOfMonth)
    }

    private fun saveData() {
        val year = datePicker.year
        val month = datePicker.month
        val day = datePicker.dayOfMonth
        val mark = markSpinner.selectedItem.toString()
        val description = descriptionEditText.text.toString()

        val userId = auth.currentUser?.uid ?: return

        val data = hashMapOf(
            "mark" to mark,
            "description" to description
        )

        db.collection("users").document(userId).collection("calendar")
            .document("$year-$month-$day")
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadData(year: Int, month: Int, day: Int) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).collection("calendar")
            .document("$year-$month-$day")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val mark = document.getString("mark") ?: "-"
                    val description = document.getString("description") ?: ""

                    markSpinner.setSelection(marks.indexOf(mark))
                    descriptionEditText.setText(description)
                } else {
                    markSpinner.setSelection(0)
                    descriptionEditText.setText("")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
