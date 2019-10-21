package com.example.administrator.vongtayhotro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_screen.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var mSharedPreferences: SharedPreferences
    //private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var DEVICE_ADDRESS:String  //địa chỉ của module HC-05
    //private val PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    //private val mFirebase = FirebaseDatabase.getInstance("https://espclient-60eeb.firebaseio.com/")
    private val mFirebase = FirebaseDatabase.getInstance("https://thang-51cf7.firebaseio.com/")
    private lateinit var mRef : DatabaseReference
    var stateOn : Boolean = false
    var stateMode : Boolean = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)
        mSharedPreferences = getSharedPreferences(Config.SharedCode, Context.MODE_PRIVATE)
        //val blueName = mSharedPreferences.getString(Config.blueName,"")
        DEVICE_ADDRESS = mSharedPreferences.getString(Config.blueAddr,"")!!
        mRef = mFirebase.getReference("status")


        //test.text = DEVICE_ADDRESS
        setSupportActionBar(tbMain)
        supportActionBar!!.title = "Vòng Tay"

        // thiết lập toggle cho navigation
        val toggle = ActionBarDrawerToggle(this@MainActivity, main_drawer, tbMain, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        main_drawer.addDrawerListener(toggle)
        toggle.syncState()

        //Thay đổi màn hình khi theo dõi dữ liệu trên Firebase
        mRef.child("Active").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Error",p0.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value =  dataSnapshot.getValue(Int::class.java)
                Log.d("AAA",value.toString())
                if(value == 1){
                    //Active = ON
                    stateOn = true
                    btnOnOff.setImageResource(R.drawable.ic_bracelet_on)
                } else {
                    //Active = OFF
                    stateOn = true
                    btnOnOff.setImageResource(R.drawable.ic_bracelet)
                    btnSound.setImageResource(R.drawable.ic_sound_off)
                    btnVibrate.setImageResource(R.drawable.vibrational_mode_off)
                }
            }
        })


        mRef.child("Mode").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Error",p0.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value =  dataSnapshot.getValue(Int::class.java)
                Log.d("AAA",value.toString())
                if(stateOn){
                    if (value == 1){
                        //Mode Sound
                        stateMode = true
                        btnSound.setImageResource(R.drawable.ic_sound_on)
                        btnVibrate.setImageResource(R.drawable.vibrational_mode_off)
                    } else {
                        //Mode Sound
                        stateMode = false
                        btnSound.setImageResource(R.drawable.ic_sound_off)
                        btnVibrate.setImageResource(R.drawable.vibrational_mode_on)
                    }
                }
            }
        })

        fabLocation.setOnClickListener {
            val intent = Intent(this@MainActivity,LocationActivity::class.java)
            startActivity(intent)
        }

        fabConnect.setOnClickListener {
            Toast.makeText(applicationContext,"Connect Bluetooth",Toast.LENGTH_SHORT).show()
        }

        btnOnOff.setOnClickListener {
            if(!stateOn){
                mRef.child("Active").setValue(1).addOnCompleteListener {
                    btnOnOff.setImageResource(R.drawable.ic_bracelet_on)
                    btnSound.setImageResource(R.drawable.ic_sound_on)
                    stateOn = true
                    mRef.child("Mode").setValue(1)
                }
            } else{
                mRef.child("Active").setValue(0).addOnCompleteListener {
                    btnOnOff.setImageResource(R.drawable.ic_bracelet)
                    btnSound.setImageResource(R.drawable.ic_sound_off)
                    btnVibrate.setImageResource(R.drawable.vibrational_mode_off)
                    stateOn = false
                    stateMode = true  // Chế độ âm thanh là mặc định
                }
            }
        }

        btnSound.setOnClickListener {
            if(stateOn){
                if(stateMode){
                    mRef.child("Mode").setValue(0).addOnCompleteListener {
                        btnSound.setImageResource(R.drawable.ic_sound_off)
                        btnVibrate.setImageResource(R.drawable.vibrational_mode_on)
                        stateMode = false
                    }
                } else {
                    mRef.child("Mode").setValue(1).addOnCompleteListener {
                        btnSound.setImageResource(R.drawable.ic_sound_on)
                        btnVibrate.setImageResource(R.drawable.vibrational_mode_off)
                        stateMode = true
                    }
                }
            } else {
                Toast.makeText(applicationContext,"Turn On First",Toast.LENGTH_SHORT).show()
            }
        }

        btnVibrate.setOnClickListener {
            if(stateOn){
                if(stateMode){
                    mRef.child("Mode").setValue(0).addOnCompleteListener {
                        btnSound.setImageResource(R.drawable.ic_sound_off)
                        btnVibrate.setImageResource(R.drawable.vibrational_mode_on)
                        stateMode = false
                    }
                } else {
                    mRef.child("Mode").setValue(1).addOnCompleteListener {
                        btnSound.setImageResource(R.drawable.ic_sound_on)
                        btnVibrate.setImageResource(R.drawable.vibrational_mode_off)
                        stateMode = true
                    }
                }
            } else{
                Toast.makeText(applicationContext,"Turn On First",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        if (main_drawer.isDrawerOpen(GravityCompat.START)) {
            main_drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.itProfile -> {
//                val intent = Intent(this@MainActivity, ProfileActivity::class.java)
//                startActivity(intent)
//            }
            R.id.itLogout -> {
                val intent = Intent(this@MainActivity, SignInActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }
    //Tạo menu và thiết lập chức năng
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.itSetting -> {
                Toast.makeText(this@MainActivity,"Clicked on Setting", Toast.LENGTH_SHORT).show()

            }
            R.id.itShareFarm -> {
                Toast.makeText(this@MainActivity,"Clicked on Share Privacy", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
