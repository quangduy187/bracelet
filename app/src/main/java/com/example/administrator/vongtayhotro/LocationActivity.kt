package com.example.administrator.vongtayhotro

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener {


    private lateinit var mMap: GoogleMap
    private var lat : Double = 0.0
    private var long : Double = 0.0
    private var mFirebase = FirebaseDatabase.getInstance("https://thang-51cf7.firebaseio.com/")

    private var service: LocationManager? = null
    private var enabled: Boolean? = null
    private var mLocationRequest: LocationRequest? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLastLocation: Location? = null
    private var mLastDeviceMarker: Marker? = null
    private var mCurrLocationMarker: Marker? = null
    private var REQUEST_LOCATION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        //Toolbar Config
        setSupportActionBar(tbLocate)
        supportActionBar!!.title = "Location"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        service = this.getSystemService(LOCATION_SERVICE) as LocationManager
        enabled = service!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFirebase.getReference("data").child("Lat").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Error",p0.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                lat =  dataSnapshot.getValue(Double::class.java)!!
                Log.d("AAA","$lat   $long")
            }
        })


        mFirebase.getReference("data").child("Long").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Error",p0.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                long =  dataSnapshot.getValue(Double::class.java)!!
                Log.d("AAA","$lat   $long")
            }
        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap.uiSettings.isZoomControlsEnabled = true
        //mMap.setMyLocationEnabled(true)

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient()
                mMap.isMyLocationEnabled = true
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        } else {
            buildGoogleApiClient()
            mMap.isMyLocationEnabled = true
        }

        val sydney = LatLng(21.0105, 105.8651)
        val option = MarkerOptions()
        option.position(sydney)
        option.title("Default Location")
        option.snippet("21.0105   105.8651")
        option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        option.alpha(0.8f)
        option.rotation(0f)
        mLastDeviceMarker = mMap.addMarker(option)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        // Check if enabled and if not send user to the GPS settings
        if (!enabled!!) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        // Check if permission is granted or not
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        Toast.makeText(this@LocationActivity,"Connection Suspended",Toast.LENGTH_SHORT).show()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(this@LocationActivity,"Connection Failed",Toast.LENGTH_SHORT).show()
    }

    override fun onLocationChanged(location: Location?) {
        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }

        //Place current location marker
        val latLng = LatLng(location!!.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("You're here")
        markerOptions.snippet("${location.latitude}  ${location.longitude}")
        markerOptions.alpha(0.8f)
        markerOptions.rotation(0f)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        mCurrLocationMarker = mMap.addMarker(markerOptions)
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    @Synchronized
    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        mGoogleApiClient!!.connect()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK") { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_LOCATION_CODE
                        )
                    }
                    .create()
                    .show()

            } else ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient()
                        }
                        mMap.isMyLocationEnabled = true
                    }
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }


    fun showLocation(googleMap: GoogleMap){
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(lat, long)
        val option = MarkerOptions()
        option.position(sydney)
        option.title("Device Location")
        option.snippet("$lat   $long")
        option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        option.alpha(0.8f)
        option.rotation(0f)
        mLastDeviceMarker!!.remove()
        mLastDeviceMarker = mMap.addMarker(option)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))
    }
    //Tạo menu và thiết lập chức năng
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.locate_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.itRefresh -> {
                Toast.makeText(this@LocationActivity,"Clicked on Refresh", Toast.LENGTH_SHORT).show()
                showLocation(mMap)
            }
            R.id.itCheck -> {
                Toast.makeText(this@LocationActivity,"Clicked on Check Bluetooth", Toast.LENGTH_SHORT).show()
            }
            R.id.itConnect -> {
                val mRef = mFirebase.getReference("status")
                mRef.child("connect").setValue(1).addOnCompleteListener {
                    Toast.makeText(this@LocationActivity,"Clicked on Connect", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.itDisConnect -> {
                val mRef = mFirebase.getReference("status")
                mRef.child("connect").setValue(0).addOnCompleteListener {
                    Toast.makeText(this@LocationActivity,"Clicked on Connect", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}