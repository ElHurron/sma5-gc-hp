package com.example.sma5.training.ui

import android.app.AlertDialog
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

class TrainingDetailsTrainerActivity : AppCompatActivity() {

    private lateinit var training: Training;

    private lateinit var txvAcceptedValues: TextView
    private lateinit var txvDeclinedValues: TextView
    private lateinit var txvDateTime: TextView
    private lateinit var txvDuration: TextView
    private lateinit var txvLocation: TextView
    private lateinit var txvTitle: TextView
    private lateinit var btnBack: ImageButton

    companion object {
        private val Id = "Role"
        private val UserName = "Username"
        fun intent(context: Context, training: Training, username: String)
                = Intent(context, TrainingDetailsTrainerActivity::class.java).apply {
            this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Id, training.id)
            putExtra(UserName, username)
        }
    }

    private fun showPlayers(players: List<String>) {
        val alertDialogBuilder = AlertDialog.Builder(this)

        alertDialogBuilder.setTitle("Players")
        alertDialogBuilder.setMessage(players.joinToString("\n"))

        // Positive Button (OK)
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            // Handle positive button click if needed
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()

        // Show the dialog
        alertDialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_details_trainer)

        txvDateTime = findViewById(R.id.txvDate)
        txvLocation = findViewById(R.id.txvLocation)
        txvAcceptedValues = findViewById(R.id.txvAcceptedValues)
        txvDeclinedValues = findViewById(R.id.txvDeclinedValues)
        txvDuration = findViewById(R.id.txvTime)
        txvTitle = findViewById(R.id.txvTitle)
        btnBack = findViewById(R.id.btnBack)

        lifecycleScope.launch {
            training = TrainingApiFactory.getApi().getTraining(intent.getStringExtra(
                TrainingDetailsTrainerActivity.Id
            )!!)!!
            txvDateTime.text = training.dateTime.replace(" ", "\n") + " Uhr"
            txvLocation.text = training.location;
            txvDuration.text = training.duration.toString() + " Stunden"
            txvAcceptedValues.text = training.acceptedUsers.size.toString()
            txvDeclinedValues.text = training.declinedUsers.size.toString()

            if(training.acceptedUsers.isNotEmpty()) {
               txvAcceptedValues.setOnClickListener{
                   showPlayers(training.acceptedUsers)
               }
            }

            if(training.declinedUsers.isNotEmpty()) {
                txvDeclinedValues.setOnClickListener{
                    showPlayers(training.declinedUsers)
                }
            }

            txvTitle.text = "Daten zu "+training.title
        }

        btnBack.setOnClickListener{
            lifecycleScope.launch {
                var user = TrainingApiFactory.getApi().getUserByName(intent.getStringExtra(
                    TrainingDetailsTrainerActivity.UserName
                )!!)!!
                var intent = TrainingOverviewActivity.intent(this@TrainingDetailsTrainerActivity, user);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }
}