package com.example.sma5.training.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sma5.training.R
import com.example.sma5.training.api.TrainingApiFactory
import com.example.sma5.training.models.SDF
import com.example.sma5.training.models.Training
import kotlinx.coroutines.launch
import java.util.Calendar


class CreateTrainingActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton;
    private lateinit var btnSave: Button
    private lateinit var edtTrainingTitle: EditText;
    private lateinit var datePicker: DatePicker;
    private lateinit var timePicker: TimePicker;
    private lateinit var edtLocation: EditText;
    private lateinit var edtDuration: EditText

    companion object {
        private val UserName = "Username"
        private val EMail = "EMail"
        fun intent(context: Context, username: String, email: String)
                = Intent(context, CreateTrainingActivity::class.java).apply {
            this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(UserName, username)
            putExtra(EMail, email)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_training)

        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        edtLocation = findViewById(R.id.edtLocation)
        timePicker = findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true)
        datePicker = findViewById(R.id.datePicker)
        edtTrainingTitle = findViewById(R.id.edtTitle);
        edtDuration = findViewById(R.id.edtDuration)

        btnBack.setOnClickListener{
            backToOverview()
        }

        btnSave.setOnClickListener {
            saveTraining()
            backToOverview()
        }
    }

    private fun saveTraining() {

        var newTraining = Training()
        newTraining.creator = intent.getStringExtra(EMail)!!
        newTraining.title = edtTrainingTitle.text.toString()
        newTraining.duration = edtDuration.text.toString().toInt()

        val day: Int = datePicker.dayOfMonth
        val month: Int = datePicker.month
        val year: Int = datePicker.year
        val hour: Int = timePicker.hour
        val minute: Int = timePicker.minute

        val calendar: Calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute, 0)

        newTraining.dateTime = SDF.format(calendar.time)
        newTraining.location = edtLocation.text.toString()

        TrainingApiFactory.getApi().insertOrUpdateTraining(newTraining)
    }

    private fun backToOverview() {
        lifecycleScope.launch {
            var user = TrainingApiFactory.getApi().getUserByName(intent.getStringExtra(UserName)!!)!!
            var intent = TrainingOverviewActivity.intent(this@CreateTrainingActivity, user);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}