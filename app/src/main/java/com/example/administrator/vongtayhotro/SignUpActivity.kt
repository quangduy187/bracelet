package com.example.administrator.vongtayhotro

import android.content.Intent
//import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnToSignIn.setOnClickListener {
            val intent = Intent(this@SignUpActivity,SignInActivity::class.java)
            startActivity(intent)
        }
    }
}
