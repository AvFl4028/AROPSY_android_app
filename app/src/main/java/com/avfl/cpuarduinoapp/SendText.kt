package com.avfl.cpuarduinoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class SendText : AppCompatActivity() {
    private lateinit var mainAct:MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_text)
        mainAct = MainActivity()
    }

    fun send(view: View){
        val data:EditText = findViewById(R.id.data)
        mainAct.sendCommand(data.text.toString())
    }

    fun back(view: View){
        finish()
    }
}