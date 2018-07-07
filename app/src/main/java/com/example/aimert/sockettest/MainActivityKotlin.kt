package com.example.aimert.sockettest

import android.app.Activity
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.lang.ref.WeakReference
import java.net.Socket

class MainActivityKotlin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                testBtnSend.isEnabled = testEditText.text.toString().isNotBlank()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        testBtnSend.setOnClickListener { sendMessage() }
    }

    private fun sendMessage() {
        SendMessageTask(this).execute()
    }

    class SendMessageTask(_activity: Activity) : AsyncTask<String, Void, String>() {

        private val activityReference = WeakReference<Activity>(_activity)

        override fun doInBackground(vararg p0: String?): String? {

            try {

                val s = Socket("84.120.70.121", 13840)

                val outputStream = s.getOutputStream()
                val writer = PrintWriter(outputStream)

                val editText: EditText = activityReference.get()!!.findViewById(R.id.testEditText)
                writer.println(editText.text)

                val inputStream = s.getInputStream()
                val reader = BufferedReader(InputStreamReader(inputStream))

                val response: String = reader.readLine()

                writer.close()
                outputStream.close()

                reader.close()
                inputStream.close()

                return response

            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: String?) {
            val textView: TextView = activityReference.get()!!.findViewById(R.id.testResponseText)
            if (result == null) {
                textView.text = "Oops! An error ocurred!"
            } else {
                textView.text = result
            }
        }
    }
}
