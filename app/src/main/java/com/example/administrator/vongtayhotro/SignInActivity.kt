package com.example.administrator.vongtayhotro

import android.content.Intent
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        btnSignIn.setOnClickListener {
            val intent = Intent(this@SignInActivity,BluetoothActivity::class.java)
            startActivity(intent)
        }

        btnToSignup.setOnClickListener {
            val intent = Intent(this@SignInActivity,SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
