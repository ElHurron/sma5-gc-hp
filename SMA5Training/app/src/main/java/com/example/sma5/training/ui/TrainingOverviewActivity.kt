package com.example.sma5.training.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.sma5.training.MainActivity
import com.example.sma5.training.R
import com.example.sma5.training.api.TrainingApiFactory
import com.example.sma5.training.models.Training
import com.example.sma5.training.models.User

class TrainingOverviewActivity : AppCompatActivity() {

    private lateinit var btnAddTraining: Button
    private lateinit var btnLogout: Button
    private val username get() = intent.getStringExtra(USERNAME) ?: ""

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

        btnAddTraining = findViewById(R.id.btnAddTraining)
        btnLogout = findViewById(R.id.btnLogout)

        btnLogout.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        btnAddTraining.setOnClickListener { addNewTraining() }
    }

    private fun addNewTraining() {
        var t = Training()
        t.title = "test"
        t.description = "test"
        t.creator = username
        TrainingApiFactory.getApi().insertTraining(t)
    }
}