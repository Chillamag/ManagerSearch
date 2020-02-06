package com.peakon.peakonmanagersearch.helpers

import com.peakon.peakonmanagersearch.models.Employee

class EmployeeComparator : Comparator<Employee> {
    // We want to sort the employees. Here is what we do:
    override fun compare(left: Employee?, right: Employee?): Int {
        // What if some of the employees doesn't exist?
        if (left == null || right == null) return 0

        // What if two employees have the same first name?
        return if (left.firstName.trim().toLowerCase() == right.firstName.trim().toLowerCase()) {
            // Then we must compare their last names
            left.lastName.toLowerCase().compareTo(right.lastName.toLowerCase())
        } else {
            // Else just compare their first names
            left.firstName.trim().toLowerCase().compareTo(right.firstName.trim().toLowerCase())
        }
    }

}