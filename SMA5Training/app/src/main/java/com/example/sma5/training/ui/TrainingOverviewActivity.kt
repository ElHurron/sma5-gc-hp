package com.example.sma5.training.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sma5.training.MainActivity
import com.example.sma5.training.R
import com.example.sma5.training.api.TrainingApiFactory
import com.example.sma5.training.models.Training
import com.example.sma5.training.models.User
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.NumberFormat

class TrainingOverviewActivity : AppCompatActivity() {

    private val trainingsAdapter = TrainingsAdapter()
    private lateinit var btnAddTraining: ImageButton
    private lateinit var btnLogout: Button
    private lateinit var srlSwipeRefreshLayout: SwipeRefreshLayout
    private var database = Firebase.database.reference
    private val trainingsKey: String = "trainings"
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

        val recyclerView: RecyclerView = findViewById(R.id.rcvTrainings);
        recyclerView.adapter = trainingsAdapter

        btnAddTraining = findViewById(R.id.btnAddTraining)
        btnLogout = findViewById(R.id.btnLogout)
        srlSwipeRefreshLayout = findViewById(R.id.selSwipeRefreshLayout)
        srlSwipeRefreshLayout.setOnRefreshListener { srlSwipeRefreshLayout.isRefreshing = false }

        btnLogout.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        btnAddTraining.setOnClickListener { addNewTraining() }

        database.child(trainingsKey).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val trainings = emptyList<Training>().toMutableList()
                for(s in snapshot.children) {
                    trainings += s.getValue(Training::class.java)!!;
                }
                trainingsAdapter.displayTrainings(trainings)
            }

            override fun onCancelled(error: DatabaseError) {
                //do nothing
            }
        })
    }

    private fun addNewTraining() {
        var t = Training()
        t.title = "test"
        t.description = "test"
        t.creator = username
        TrainingApiFactory.getApi().insertTraining(t)
    }


    private class TrainingsAdapter: RecyclerView.Adapter<TrainingsAdapter.ViewHolder>()
    {
        private class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val txvTitle: TextView = itemView.findViewById(R.id.txvTitle)
        }

        private var trainings = emptyList<Training>()

        //also possible as one-liner
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_training, parent, false))
        }

        //override fun getItemCount() = canteens.size
        override fun getItemCount(): Int {
            return trainings.size
        }

        //combine view and datamodel
        //holder.run makes holder as base variable (don't need to write holder)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.run {
            val training = trainings[position]
            txvTitle.text = training.title;

            /*itemView.setOnClickListener {
                itemView.context.run {
                    startActivity(CanteenDetailsActivity.intent(this, canteen.id))
                }
            }*/
        }

        fun displayTrainings(canteens: List<Training>) {
            this.trainings = canteens
            notifyDataSetChanged()
        }
    }
}