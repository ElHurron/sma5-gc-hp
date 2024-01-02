package com.example.sma5.training.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sma5.training.R
import com.example.sma5.training.models.User

class TrainingOverviewActivity : AppCompatActivity() {

    companion object {
        private val ROLE = "Role"
        private val USERNAME = "Username"

        fun intent(context: Context, user: User)
                = Intent(context, TrainingOverviewActivity::class.java).apply {
            putExtra(ROLE, user.role);
            putExtra(USERNAME, user.username)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_overview)
    }
}