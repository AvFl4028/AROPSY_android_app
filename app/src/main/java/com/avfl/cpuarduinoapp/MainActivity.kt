package com.avfl.cpuarduinoapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var btAdapter: BluetoothAdapter
    private var mAddressDevices: ArrayAdapter<String>? = null
    private var mNameDevices: ArrayAdapter<String>? = null
    private lateinit var spinDevices: Spinner
    private var intValSpin:Int = 0

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private var m_bluetoothSocket: BluetoothSocket? = null

        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        spinDevices = findViewById(R.id.devicesList)

        ArrayAdapter.createFromResource(this, R.array.noDevices, android.R.layout.simple_list_item_1).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinDevices.adapter = adapter
        }

    }

    fun refresh(view: View) {
        if (btAdapter.isEnabled) {
            Toast.makeText(this, "Bluetooth is enable", Toast.LENGTH_SHORT).show()
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i("MainActivity", "Activity")
            }
        }

        mAddressDevices = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        mNameDevices = ArrayAdapter(this, android.R.layout.simple_list_item_1)

        if (btAdapter.isEnabled) {
            val pairedDevice: Set<BluetoothDevice> = btAdapter.bondedDevices
            mAddressDevices!!.clear()
            mNameDevices!!.clear()

            pairedDevice.forEach { device ->
                val deviceName = device.name
                val deviceAddress = device.address

                mAddressDevices!!.add(deviceAddress)
                mNameDevices!!.add(deviceName)
            }

            spinDevices.adapter = mNameDevices

        } else {
            val noDevices = "There's no available devices"
            mAddressDevices!!.add(noDevices)
            mNameDevices!!.add(noDevices)

            if(!btAdapter.isEnabled){
                Toast.makeText(this, "Bluetooth is disable", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this, "No devices connected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun connect(view: View) {
        val spinDevices: Spinner = findViewById(R.id.devicesList)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i("MainActivity", "Activity")
        }

        if (btAdapter.isEnabled) {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    intValSpin = spinDevices.selectedItemPosition

                    if(mAddressDevices == null){
                        return
                    }

                    m_address = mAddressDevices!!.getItem(intValSpin).toString()
                    Toast.makeText(this, m_address, Toast.LENGTH_SHORT).show()
                    btAdapter.cancelDiscovery()

                    if(m_address == "There's no available devices"){
                        return
                    }

                    val device = btAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    m_bluetoothSocket!!.connect()
                }

                Toast.makeText(this, "Connection is ready!", Toast.LENGTH_SHORT).show()

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Bluetooth is activating", Toast.LENGTH_SHORT).show()
            btAdapter.enable()
        }

    }

    fun btnDisconnect(view: View) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i("MainActivity", "Activity")
        }
        if (btAdapter.isEnabled) {
            btAdapter.disable()
            Toast.makeText(this, "Bluetooth is now disable", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Bluetooth was disable before", Toast.LENGTH_SHORT).show()
        }

    }

    fun btnUp(view: View) {
        sendCommand("1")
    }

    fun btnDown(view: View) {
        sendCommand("2")
    }

    fun btnEnter(view: View) {
        sendCommand("3")
    }

    fun btnBack(view: View) {
        sendCommand("4")
    }

    fun sendTextActivity(view: View){
        val intent = Intent(this, SendText::class.java)
        startActivity(intent)
    }

fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }else{
            Toast.makeText(this, "No connected", Toast.LENGTH_SHORT).show()
        }
    }
}