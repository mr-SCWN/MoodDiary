package edu.put.mooddiary

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*

class RaportPage : AppCompatActivity() {

    private lateinit var startDaySpinner: Spinner
    private lateinit var startMonthSpinner: Spinner
    private lateinit var startYearSpinner: Spinner
    private lateinit var endDaySpinner: Spinner
    private lateinit var endMonthSpinner: Spinner
    private lateinit var endYearSpinner: Spinner
    private lateinit var calculateButton: Button
    private lateinit var clearAllButton: Button
    private lateinit var chartContainer: LinearLayout
    private lateinit var backButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_raport_page)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        startDaySpinner = findViewById(R.id.startDaySpinner)
        startMonthSpinner = findViewById(R.id.startMonthSpinner)
        startYearSpinner = findViewById(R.id.startYearSpinner)
        endDaySpinner = findViewById(R.id.endDaySpinner)
        endMonthSpinner = findViewById(R.id.endMonthSpinner)
        endYearSpinner = findViewById(R.id.endYearSpinner)
        calculateButton = findViewById(R.id.calculateButton)
        clearAllButton = findViewById(R.id.clearAllButton)
        chartContainer = findViewById(R.id.chartContainer)
        backButton = findViewById(R.id.openMainActivityButton2)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        setupSpinners()

        calculateButton.setOnClickListener {
            calculateStatistics()
        }

        clearAllButton.setOnClickListener {
            clearAll()
        }

        setTodayDate()
    }

    private fun setupSpinners() {
        // Initialize arrays for days, months, and years
        val days = (1..31).toList().map { it.toString() }
        val months = resources.getStringArray(R.array.months).toList()
        val years = (2000..Calendar.getInstance().get(Calendar.YEAR)).toList().map { it.toString() }

        // Set up adapters for spinners
        startDaySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        startMonthSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        startYearSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        endDaySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        endMonthSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        endYearSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)

        // Set initial selection to today
        val today = Calendar.getInstance()
        startDaySpinner.setSelection(today.get(Calendar.DAY_OF_MONTH) - 1)
        startMonthSpinner.setSelection(today.get(Calendar.MONTH))
        startYearSpinner.setSelection(years.indexOf(today.get(Calendar.YEAR).toString()))
        endDaySpinner.setSelection(today.get(Calendar.DAY_OF_MONTH) - 1)
        endMonthSpinner.setSelection(today.get(Calendar.MONTH))
        endYearSpinner.setSelection(years.indexOf(today.get(Calendar.YEAR).toString()))
    }

    private fun calculateStatistics() {
        val startDate = getDateFromSpinners(startDaySpinner, startMonthSpinner, startYearSpinner)
        val endDate = getDateFromSpinners(endDaySpinner, endMonthSpinner, endYearSpinner)

        // Add one day to endDate to include the entire day in the query
        val calendar = Calendar.getInstance()
        calendar.time = endDate
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val adjustedEndDate = calendar.time

        if (startDate > endDate) {
            Toast.makeText(this, "Start date cannot be after end date", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).collection("calendar")
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", adjustedEndDate)
            .get()
            .addOnSuccessListener { documents ->
                val markCounts = IntArray(9)
                for (document in documents) {
                    val mark = document.getString("mark") ?: "-"
                    if (mark != "-") {
                        val markInt = mark.toIntOrNull()
                        if (markInt != null && markInt in 1..9) {
                            markCounts[markInt - 1]++
                        }
                    }
                }
                displayChart(markCounts)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayChart(markCounts: IntArray) {
        chartContainer.removeAllViews()

        val barEntries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        for (i in markCounts.indices) {
            barEntries.add(BarEntry(i.toFloat(), markCounts[i].toFloat()))
            labels.add("em${i + 1}")
        }

        val barDataSet = BarDataSet(barEntries, "Marks")
        barDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        barDataSet.valueTextSize = 16f
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        val barData = BarData(barDataSet)

        val barChart = BarChart(this)
        barChart.data = barData
        barChart.setFitBars(true)
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.granularity = 1f
        barChart.xAxis.setLabelCount(labels.size)
        barChart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM // Set labels to the bottom
        barChart.axisLeft.granularity = 1f
        barChart.axisRight.isEnabled = false
        barChart.description = Description().apply { text = "" }
        barChart.legend.isEnabled = false

        chartContainer.addView(barChart)
    }


    private fun clearAll() {
        setTodayDate()
        chartContainer.removeAllViews()
    }

    private fun setTodayDate() {
        val today = Calendar.getInstance()
        startDaySpinner.setSelection(today.get(Calendar.DAY_OF_MONTH) - 1)
        startMonthSpinner.setSelection(today.get(Calendar.MONTH))
        startYearSpinner.setSelection((2000..Calendar.getInstance().get(Calendar.YEAR)).toList().indexOf(today.get(Calendar.YEAR)))
        endDaySpinner.setSelection(today.get(Calendar.DAY_OF_MONTH) - 1)
        endMonthSpinner.setSelection(today.get(Calendar.MONTH))
        endYearSpinner.setSelection((2000..Calendar.getInstance().get(Calendar.YEAR)).toList().indexOf(today.get(Calendar.YEAR)))
    }

    private fun getDateFromSpinners(daySpinner: Spinner, monthSpinner: Spinner, yearSpinner: Spinner): Date {
        val day = daySpinner.selectedItem.toString().toInt()
        val month = monthSpinner.selectedItemPosition
        val year = yearSpinner.selectedItem.toString().toInt()
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return calendar.time
    }
}
