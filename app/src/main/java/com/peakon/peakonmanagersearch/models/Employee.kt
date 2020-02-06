package com.peakon.peakonmanagersearch.models

class Employee(id: String, first_name: String, last_name: String) {

    var firstName: String       = first_name
    var lastName: String        = last_name
    var jobLevel: String        = ""
    var jobDepartment: String   = ""
    var businessUnit: String    = ""

    var email: String = ""
}