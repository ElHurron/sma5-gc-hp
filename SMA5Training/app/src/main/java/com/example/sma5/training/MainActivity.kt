package com.example.sma5.training

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var btnSignUp: Button
    private lateinit var btnSignIn: Button
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSignUp = findViewById(R.id.buttonSignUp)
        btnSignIn = findViewById(R.id.buttonSignIn)
        txtEmail = findViewById(R.id.fieldEmail)
        txtPassword = findViewById(R.id.fieldPassword)
    }
}