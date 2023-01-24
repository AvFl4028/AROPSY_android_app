package com.avfl.cpuarduinoapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var btAdapter: BluetoothAdapter
    private var mAddressDevices: ArrayAdapter<String>? = null
    private var mNameDevices: ArrayAdapter<String>? = null

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private var m_bluetoothSocket: BluetoothSocket? = null

        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val refresh:ImageButton = findViewById(R.id.refresh)
        val connect: Button = findViewById(R.id.connect)
        val spinDevices:Spinner = findViewById(R.id.devicesList)
        val btnUp:Button = findViewById(R.id.up)
        val btnDown: Button = findViewById(R.id.down)
        val btnEnter: Button = findViewById(R.id.enter)
        val btnBack: Button = findViewById(R.id.back)

        mAddressDevices = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        mNameDevices = ArrayAdapter(this, android.R.layout.simple_list_item_1)

        btAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        if (btAdapter.isEnabled){
            Toast.makeText(this, "Bluetooth is enable", Toast.LENGTH_SHORT).show()
        }else{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
                Log.i("MainActivity", "Activity")
            }
        }


        refresh.setOnClickListener{
            if (btAdapter.isEnabled){
                val pairedDevice:Set<BluetoothDevice> = btAdapter.bondedDevices
                mAddressDevices!!.clear()
                mNameDevices!!.clear()

                pairedDevice.forEach{
                    device ->
                    val deviceName = device.name
                    val deviceAddress = device.address

                    mAddressDevices!!.add(deviceAddress)
                    mNameDevices!!.add(deviceName)
                }
                spinDevices.adapter = mNameDevices
            }
            else
            {
                val noDevices = "No hay dispositivos disponibles"
                mAddressDevices!!.add(noDevices)
                mNameDevices!!.add(noDevices)
                Toast.makeText(this, "No devices connected", Toast.LENGTH_SHORT).show()
            }
        }

        connect.setOnClickListener{
            try {
                if (m_bluetoothSocket == null || !m_isConnected){

                    val intValSpin = spinDevices.selectedItemPosition
                    m_address = mAddressDevices!!.getItem(intValSpin).toString()
                    Toast.makeText(this, m_address, Toast.LENGTH_SHORT).show()
                    btAdapter.cancelDiscovery()

                    val device = btAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    m_bluetoothSocket!!.connect()
                }
                Toast.makeText(this, "Connection is ready!", Toast.LENGTH_SHORT).show()
            }catch (e: IOException){
                e.printStackTrace()
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show()
            }
        }

        btnUp.setOnClickListener{
            sendCommand("1")
        }

        btnDown.setOnClickListener{
            sendCommand("2")
        }
        btnEnter.setOnClickListener{
            sendCommand("3")
        }
        btnBack.setOnClickListener{
            sendCommand("4")
        }
    }

    private fun sendCommand(input:String){
        if (m_bluetoothSocket != null){
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }
}