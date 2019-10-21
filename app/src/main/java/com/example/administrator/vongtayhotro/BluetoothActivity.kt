package com.example.administrator.vongtayhotro

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.SharedPreferences
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
//import android.support.v7.widget.DefaultItemAnimator
//import android.support.v7.widget.DividerItemDecoration
//import android.support.v7.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.administrator.vongtayhotro.R.color.gray
import com.example.administrator.vongtayhotro.R.color.light_blue
import kotlinx.android.synthetic.main.activity_bluetooth.*
//
class BluetoothActivity : AppCompatActivity() ,BlueAdapter.AdapterCallback{

    lateinit var mSharedPreferences: SharedPreferences
    private var btAdapter: BluetoothAdapter? = null
    private var blueState = false
    private lateinit var pairedDevice: Set<BluetoothDevice>

    override fun onMethodCallback() {

        val blueName = mSharedPreferences.getString(Config.blueName,"")
        val blueAddr = mSharedPreferences.getString(Config.blueAddr,"")
        Log.d("BBB", "$blueName + $blueAddr")
        Toast.makeText(this@BluetoothActivity, "$blueName  $blueAddr",Toast.LENGTH_LONG).show()
        sendToMain()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        mSharedPreferences = getSharedPreferences(Config.SharedCode, Context.MODE_PRIVATE)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler)

        btAdapter = BluetoothAdapter.getDefaultAdapter()
        // check if your device support bluetooth or not
        if (btAdapter != null) {
            Toast.makeText(this@BluetoothActivity, "Bluetooth is available", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this@BluetoothActivity, "Your device does not support bluetooth", Toast.LENGTH_SHORT).show()
        }

        //Hàm bật tắt bluetooth
        btnTurnOn.setOnClickListener {
            if(!blueState){
                if (!btAdapter!!.isEnabled) {
                    val turnOn = Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(turnOn, 0)
                    // có hiện thị muốn bật hay không nên cần bắt lỗi này
                    btnTurnOn.text = "Turn On"
                    btnTurnOn.background = resources.getDrawable(light_blue)
                    blueState = true
                    Toast.makeText(this@BluetoothActivity, "Turn On Bluetooth", Toast.LENGTH_SHORT).show()
                } else {
                    blueState = true
                    Toast.makeText(this@BluetoothActivity, "Bluetooth's Already On", Toast.LENGTH_SHORT).show()
                }

            }
            else {
                btAdapter!!.disable()
                btnTurnOn.text = "Turn Off"
                btnTurnOn.background = resources.getDrawable(gray)
                blueState = false
                Toast.makeText(this@BluetoothActivity, "Turn Off", Toast.LENGTH_SHORT).show()
            }
        }

        //Hàm cho phép các thiết bị khác nhìn thấy mình
        btnVisible.setOnClickListener {
            if (blueState) {
//                val getVisible = Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
//                startActivityForResult(getVisible, 0)
                pairedDevice = btAdapter!!.bondedDevices
                val list: ArrayList<BluetoothDevice> = ArrayList()
                for (bt: BluetoothDevice in pairedDevice) {
                    list.add(bt)
                    Log.d("AAA","${bt.name} + ${bt.address}")
                }
                recyclerView.setHasFixedSize(true)
                val dividerItemDecoration = DividerItemDecoration(recycler.context, DividerItemDecoration.VERTICAL)
                recyclerView.addItemDecoration(dividerItemDecoration)
                val itemAnimator = DefaultItemAnimator()
                recyclerView.itemAnimator = itemAnimator
                recyclerView.layoutManager = LinearLayoutManager(this@BluetoothActivity, RecyclerView.VERTICAL, false)
                recyclerView.adapter = BlueAdapter(list,this@BluetoothActivity)
            } else {
                Toast.makeText(this@BluetoothActivity, "Turn on Bluetooth First", Toast.LENGTH_SHORT).show()
            }
        }

        //Chuyển sang main nếu sử dụng Wifi
        btnBluetooth.setOnClickListener{
            sendToMain()
        }
    }
    private fun sendToMain(){
        val intent  = Intent(this@BluetoothActivity,MainActivity::class.java)
        startActivity(intent)
    }
}
