package com.example.sma5.training

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.sma5.training.models.User
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

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

        btnSignUp.setOnClickListener{signUp()}
        btnSignIn.setOnClickListener{signIn()}

        database = Firebase.database.reference
        auth = Firebase.auth
    }

    private fun signIn() {
        Log.d(TAG, "signIn")
        if (!validateForm()) {
            return
        }

        val email = txtEmail.text.toString()
        val password = txtPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                Log.d(TAG, "signIn:onComplete:" + task.isSuccessful)

                if (task.isSuccessful) {
                    onAuthSuccess(task.result?.user!!)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Sign In Failed",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun signUp() {
        Log.d(TAG, "signUp")
        if (!validateForm()) {
            return
        }

        val email = txtEmail.text.toString()
        val password = txtPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                Log.d(TAG, "createUser:onComplete:" + task.isSuccessful)

                if (task.isSuccessful) {
                    onAuthSuccess(task.result?.user!!)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Sign Up Failed",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun onAuthSuccess(user: FirebaseUser) {
        val username = usernameFromEmail(user.email!!)

        // Write new user
        writeNewUser(user.uid, username, user.email)
    }

    private fun usernameFromEmail(email: String): String {
        return if (email.contains("@")) {
            email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            email
        }
    }

    private fun validateForm(): Boolean {
        var result = true
        if (TextUtils.isEmpty(txtEmail.text.toString())) {
            txtEmail.error = "Required"
            result = false
        } else {
            txtEmail.error = null
        }

        if (TextUtils.isEmpty(txtPassword.text.toString())) {
            txtPassword.error = "Required"
            result = false
        } else {
            txtPassword.error = null
        }

        return result
    }

    private fun writeNewUser(userId: String, name: String, email: String?) {
        val user = User(name, email)
        database.child("users").child(userId).setValue(user)
    }
}