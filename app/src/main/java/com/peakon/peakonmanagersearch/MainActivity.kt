package com.peakon.peakonmanagersearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ListView
import com.peakon.peakonmanagersearch.adapters.EmployeeListAdapter
import com.peakon.peakonmanagersearch.helpers.EmployeeComparator
import com.peakon.peakonmanagersearch.models.Employee
import com.peakon.peakonmanagersearch.utils.DatabaseUtils
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: EmployeeListAdapter
    private val emailMap: HashMap<String, String> = HashMap()
    private val employeeList: MutableList<Employee> = ArrayList()

    private lateinit var searchEmployeeEditText: TextInputEditText
    private var searchedForString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListVew()
        setupSearchEditText()
        getAllData()
    }

    private fun setupListVew() {
        listView = findViewById(R.id.list_view)
        adapter = EmployeeListAdapter(this, employeeList)
        listView.adapter = adapter
    }

    private fun setupSearchEditText() {
        searchEmployeeEditText = findViewById(R.id.search_edit_text)
        searchEmployeeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // When we type in some characters, we want to filter
                // the list to the employees, we are looking for
                searchedForString = if(s!!.isNotEmpty()) {
                    s.toString().toLowerCase()
                } else {
                    ""
                }
                // Send the string, we searched for, to the adapter to filter
                adapter.filter.filter(searchedForString)
            }
        })
    }

    private fun addEmailsToMap(included: JSONArray) {
        // The emails and the employees are connected through their account
        // We need to save the account ids in a Map with the emails
        for (i in 0 until included.length()) {
            val account                         = included.getJSONObject(i)
            val accountId                       = account.getString("id")
            val accountAttributes               = account.getJSONObject("attributes")

            try {
                val accountEmail                = accountAttributes.getString("email")
                emailMap[accountId]             = accountEmail
            } catch (e: JSONException) {}
        }
    }

    private fun addEmployeesToMap(data: JSONArray) {
        // Here we collect all the data from the employees
        for (i in 0 until data.length()) {
            val employeeData                    = data.getJSONObject(i)
            val id                              = employeeData.getString("id")
            val attributes                      = employeeData.getJSONObject("attributes")
            var jobLevel = ""
            var jobDepartment = ""
            var businessUnit = ""

            var shouldContinue = true
            try {
                // We only want the real employees. Not the dummies.
                // Real employees have Job Levels, right?
                jobLevel        = attributes.getString("Job Level")
                jobDepartment   = attributes.getString("Department")
                businessUnit    = attributes.getString("Business Unit")
            } catch (e: JSONException) {
                shouldContinue = false
            }

            if(shouldContinue) {
                // Here we fetch all the employees data and save it
                val firstName                   = attributes.get("firstName").toString()
                val lastName                    = attributes.get("lastName").toString()
                val employee                    = Employee(id, firstName, lastName)
                employee.jobLevel               = jobLevel
                employee.jobDepartment          = jobDepartment
                employee.businessUnit           = businessUnit
                val relationship                = employeeData.getJSONObject("relationships")
                val accountId                   = relationship.getJSONObject("account")
                    .getJSONObject("data").getString("id")

                val email: String? = emailMap[accountId]

                // Now we need the connected email for the employee
                if(email!=null) employee.email = email

                // Add the employee to the list of employees!
                employeeList.add(employee)
                // Remember to sort the employees, so it looks nicer
                Collections.sort(employeeList, EmployeeComparator())
                // Remember to notify the adapter, that new data is available
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun getAllData() {
        // Connect to the database, and retrieve the data
        val databaseUtils = DatabaseUtils()
        databaseUtils.getAllData( object : DatabaseUtils.DatabaseCallback {
            override fun onDataReceived(responseData: String) {
                runOnUiThread { handleData(responseData) }
            }

            override fun onFailure(e: IOException) {
                throw IOException("Loading database failure")
            }

            override fun onNoData() {
                println("No data received")
            }

        })
    }

    private fun handleData(responseData: String) {
        // Okay, we have the data. Now what?
        try {
            val json        = JSONObject(responseData)
            val data        = json.getJSONArray("data")
            val included    = json.getJSONArray("included")
            addEmailsToMap(included)
            addEmployeesToMap(data)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
