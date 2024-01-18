package com.example.sma5.training.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.TimePicker
import androidx.lifecycle.lifecycleScope
import com.example.sma5.training.R
import com.example.sma5.training.api.TrainingApiFactory
import com.example.sma5.training.models.SDF
import com.example.sma5.training.models.Training
import kotlinx.coroutines.launch
import java.util.Calendar

class EditTrainingActivity : AppCompatActivity() {

    companion object {
        private val UserName = "Username"
        private val Training = "Training"
        fun intent(context: Context, username: String, training: Training)
                = Intent(context, EditTrainingActivity::class.java).apply {
            this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(UserName, username)
            putExtra(Training, training.id)
        }
    }

    private lateinit var training: Training
    private lateinit var btnSave: Button
    private lateinit var edtTrainingTitle: EditText;
    private lateinit var datePicker: DatePicker;
    private lateinit var timePicker: TimePicker;
    private lateinit var edtLocation: EditText;
    private lateinit var edtDuration: EditText
    private lateinit var btnDelete: Button
    private lateinit var btnBack: ImageButton
    private lateinit var txvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_training)

        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        edtLocation = findViewById(R.id.edtLocation)
        timePicker = findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true)
        datePicker = findViewById(R.id.datePicker)
        edtTrainingTitle = findViewById(R.id.edtTitle);
        edtDuration = findViewById(R.id.edtDuration)
        btnDelete = findViewById(R.id.btnDelete)
        txvTitle = findViewById(R.id.txvTitle)

        btnBack.setOnClickListener{backToOverview()}
        btnDelete.setOnClickListener{backToOverview()}

        lifecycleScope.launch {
            training = TrainingApiFactory.getApi().getTraining(intent.getStringExtra(Training)!!)!!
            edtTrainingTitle.setText(training.title)
            txvTitle.text = training.title + " bearbeiten"
            edtLocation.setText(training.location)
            val date = SDF.parse(training.dateTime)!!

            val calendar = Calendar.getInstance()
            calendar.time = date

            datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            timePicker.minute = calendar.get(Calendar.MINUTE)
            timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
            edtDuration.setText(training.duration.toString())
            btnSave.setOnClickListener{
                updateTraining()
                backToOverview()
            }
        }
    }

    private fun updateTraining() {

        var newTraining = Training()
        newTraining.id = training.id
        newTraining.creator = intent.getStringExtra(EditTrainingActivity.UserName)!!
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
            var user = TrainingApiFactory.getApi().getUserByName(intent.getStringExtra(
                UserName
            )!!)!!
            var intent = TrainingOverviewActivity.intent(this@EditTrainingActivity, user);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}