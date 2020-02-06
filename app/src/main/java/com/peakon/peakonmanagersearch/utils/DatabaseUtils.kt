package com.peakon.peakonmanagersearch.utils

import okhttp3.*
import java.io.IOException

class DatabaseUtils {

    private val client = OkHttpClient()
    private val url = "https://gist.githubusercontent.com/daviferreira/41238222ac31fe36348544ee1d4a9a5e/raw/5dc996407f6c9a6630bfcec56eee22d4bc54b518/employees.json"

    // Create the database interface to send the data back
    interface DatabaseCallback {
        fun onDataReceived(responseData: String)
        fun onFailure(e: IOException)
        fun onNoData()
    }

    // Connect to the database and retrieve the data
    fun getAllData(callback: DatabaseCallback) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request)
            .enqueue( object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val responseData = response.body()?.string()
                    if(responseData != null) {
                        // Data received. Send it back where we need it
                        callback.onDataReceived(responseData)
                    } else {
                        // We did not get any data back from the database
                        callback.onNoData()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    // We failed getting the data
                    callback.onFailure(e)
                }
            })
    }
}