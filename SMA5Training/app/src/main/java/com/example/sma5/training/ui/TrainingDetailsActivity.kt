package com.example.sma5.training.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.sma5.training.R
import com.example.sma5.training.api.TrainingApiFactory
import com.example.sma5.training.models.Training
import kotlinx.coroutines.launch

class TrainingDetailsActivity : AppCompatActivity() {

    private lateinit var training: Training;

    private lateinit var txvGroup: TextView
    private lateinit var txvDateTime: TextView
    private lateinit var txvDuration: TextView
    private lateinit var txvLocation: TextView
    private lateinit var txvTitle: TextView
    private lateinit var btnBack: ImageButton
    companion object {
        private val Id = "Role"
        private val UserName = "Username"
        fun intent(context: Context, training: Training, username: String)
                = Intent(context, TrainingDetailsActivity::class.java).apply {
                    this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(Id, training.id)
                    putExtra(UserName, username)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_details)

        txvDateTime = findViewById(R.id.txvDate)
        txvLocation = findViewById(R.id.txvLocation)
        txvGroup = findViewById(R.id.txvGroup)
        txvDuration = findViewById(R.id.txvTime)
        txvTitle = findViewById(R.id.txvTrainingTitle)
        btnBack = findViewById(R.id.btnBack)

        lifecycleScope.launch {
            training = TrainingApiFactory.getApi().getTraining(intent.getStringExtra(Id)!!)!!
            txvDateTime.text = training.dateTime.replace(" ", "\n") + " Uhr"
            txvLocation.text = training.location;
            txvDuration.text = training.duration.toString() + " Stunden"
            txvGroup.text = training.acceptedUsers.size.toString() + " haben zugesagt!"
            txvTitle.text = training.title
        }

        btnBack.setOnClickListener{
            lifecycleScope.launch {
                var user = TrainingApiFactory.getApi().getUserByName(intent.getStringExtra(UserName)!!)!!
                var intent = TrainingOverviewActivity.intent(this@TrainingDetailsActivity, user);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }
}