package com.peakon.peakonmanagersearch.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.peakon.peakonmanagersearch.R
import com.peakon.peakonmanagersearch.models.Employee

class EmployeeListAdapter(context: Context, private val employeeList: List<Employee>) : BaseAdapter(), Filterable {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var employeeListFiltered: List<Employee> = employeeList

    override fun getView(position: Int, v: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.list_item_employee, parent, false)

        val employeeNameTextView            = view.findViewById<TextView>(R.id.employee_name_text_view)
        val employeeEmailTextView           = view.findViewById<TextView>(R.id.employee_email_text_view)
        val employeeJobLevelTextView        = view.findViewById<TextView>(R.id.employee_job_level_text_view)
        val employeeJobDepartmentTextView   = view.findViewById<TextView>(R.id.employee_job_department_text_view)
        val employeeBusinessUnitTextView    = view.findViewById<TextView>(R.id.employee_business_unit_text_view)

        val employee = getItem(position) as Employee

        val employeeName                    = "${employee.firstName} ${employee.lastName}"
        employeeNameTextView.text           = employeeName
        employeeEmailTextView.text          = employee.email
        employeeJobLevelTextView.text       = employee.jobLevel
        employeeJobDepartmentTextView.text  = employee.jobDepartment
        employeeBusinessUnitTextView.text   = employee.businessUnit

        return view
    }

    override fun getItem(position: Int): Any {
        return employeeListFiltered[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return employeeListFiltered.size
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                employeeListFiltered = filterResults.values as List<Employee>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                if (queryString!!.isEmpty()) employeeListFiltered = employeeList
                else {
                    val filteredList: MutableList<Employee> = ArrayList()
                    for (employee in employeeList) {
                        if(employee.firstName.toLowerCase().contains(queryString)
                            || employee.lastName.toLowerCase().contains(queryString)
                            || employee.email.toLowerCase().contains(queryString)) {
                            filteredList.add(employee)
                        }
                    }
                    employeeListFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = employeeListFiltered
                return filterResults
            }
        }
    }

}