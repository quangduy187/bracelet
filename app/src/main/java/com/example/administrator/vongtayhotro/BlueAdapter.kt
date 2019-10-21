package com.example.administrator.vongtayhotro

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.SharedPreferences
//import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BlueAdapter(var list: ArrayList<BluetoothDevice>, var context : Context) : RecyclerView.Adapter<BlueAdapter.BluetoothViewholder>() {

    var mAdapterCallBack: AdapterCallback
    lateinit var mSharedPreferences: SharedPreferences

    init {
        this.mAdapterCallBack = context as AdapterCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BluetoothViewholder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_bluetooth, parent, false)

        return BluetoothViewholder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BluetoothViewholder, position: Int) {
        val bluetoothName = list[position].name
        val bluetoothAddr = list[position].address
        holder.blueName.text = bluetoothName
        holder.blueAddr.text = bluetoothAddr
        mSharedPreferences = context.getSharedPreferences(Config.SharedCode, Context.MODE_PRIVATE)
        holder.imgBlue.setOnClickListener {
            saveSharedPreStatus(bluetoothName,bluetoothAddr)
            mAdapterCallBack.onMethodCallback()
        }
    }

    private fun saveSharedPreStatus(name: String, addr : String) {
        val editor = mSharedPreferences.edit()
        editor.putString(Config.blueName, name)
        editor.putString(Config.blueAddr, addr)
        editor.apply()
    }

    interface AdapterCallback {
        fun onMethodCallback()
    }


    inner class BluetoothViewholder(item: View): RecyclerView.ViewHolder(item){
        val blueAddr = item.findViewById<View>(R.id.blueAddr) as TextView
        val blueName = item.findViewById<View>(R.id.blueName) as TextView
        val imgBlue = item.findViewById<View>(R.id.imgBlue) as ImageButton
    }
}