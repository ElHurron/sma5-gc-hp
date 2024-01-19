package com.example.sma5.training.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sma5.training.MainActivity
import com.example.sma5.training.R
import com.example.sma5.training.api.TrainingApiFactory
import com.example.sma5.training.models.Roles
import com.example.sma5.training.models.SDF
import com.example.sma5.training.models.Training
import com.example.sma5.training.models.User
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import java.util.Calendar

class TrainingOverviewActivity : AppCompatActivity() {

    private val trainingsAdapter = TrainingsAdapter(this)
    private lateinit var btnAddTraining: ImageButton
    private lateinit var btnLogout: Button
    private lateinit var srlSwipeRefreshLayout: SwipeRefreshLayout
    private var database = Firebase.database.reference
    private val trainingsKey: String = "trainings"
    public val username get() = intent.getStringExtra(USERNAME) ?: ""
    lateinit var user: User;

    companion object {
        private val ROLE = "Role"
        private val USERNAME = "Username"
        fun intent(context: Context, user: User)
                = Intent(context, TrainingOverviewActivity::class.java).apply {
            this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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

        lifecycleScope.launch {
            user = TrainingApiFactory.getApi().getUserByName(username)!!;
            btnAddTraining.isVisible = user.role == Roles.TRAINER

            database.child(trainingsKey).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val trainings = emptyList<Training>().toMutableList()
                    for(s in snapshot.children) {
                        val training = s.getValue(Training::class.java)!!
                        if(SDF.parse(training.dateTime).after(Calendar.getInstance().time)) {
                            trainings += training
                        }
                    }

                    val trainingsAdapter = TrainingsAdapter(this@TrainingOverviewActivity)
                    recyclerView.adapter = trainingsAdapter
                    trainingsAdapter.displayTrainings(trainings)
                }

                override fun onCancelled(error: DatabaseError) {
                    //do nothing
                }
            })
        }

        btnLogout.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        btnAddTraining.setOnClickListener { addNewTraining() }
    }

    private fun addNewTraining() {
        var intent=CreateTrainingActivity.intent(this, username, user.email!!)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }


    private class TrainingsAdapter: RecyclerView.Adapter<TrainingsAdapter.ViewHolder>
    {
        private val parentView: TrainingOverviewActivity;
        constructor(parent: TrainingOverviewActivity) {
            parentView = parent;
        }

        private class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val txvTitle: TextView = itemView.findViewById(R.id.txvTitle)
            val btnAcceptTraining: ImageButton = itemView.findViewById(R.id.btnAccept)
            val btnDeclineTraining: ImageButton = itemView.findViewById(R.id.btnDecline)
            val btnEditTraining: ImageButton = itemView.findViewById(R.id.btnEdit)
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

            btnEditTraining.isVisible =
                (parentView.user.role == Roles.TRAINER) &&
                        (training.creator == parentView.user.email!!)

            if(training.acceptedUsers.contains(parentView.user.email!!)) {
                btnAcceptTraining.isVisible = false;
            }

            if(training.declinedUsers.contains(parentView.user.email!!)) {
                btnDeclineTraining.isVisible = false;
            }

            btnAcceptTraining.setOnClickListener {
                updateTraining(training, true)
                btnAcceptTraining.isVisible = false
                btnDeclineTraining.isVisible = true
            }
            btnDeclineTraining.setOnClickListener {
                updateTraining(training, false)
                btnDeclineTraining.isVisible = false
                btnAcceptTraining.isVisible = true
            }

            btnEditTraining.setOnClickListener{
                itemView.context.run {
                    var intent = EditTrainingActivity.intent(this, parentView.user.username!!, training)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    parentView.startActivity(intent)
                }
            }

            itemView.setOnClickListener {
                itemView.context.run {
                    if (parentView.user.role == Roles.TRAINER && training.creator == parentView.user.email!!) {
                        var intent = TrainingDetailsTrainerActivity.intent(this, training, parentView.user.username!!)
                        startActivity(intent)
                    } else {
                        var intent = TrainingDetailsActivity.intent(this, training, parentView.user.username!!)
                        startActivity(intent)
                    }
                }
            }
        }

        private fun updateTraining(training: Training, accepted: Boolean) {
            parentView.lifecycleScope.launch {
                TrainingApiFactory.getApi().addUserToTraining(training.id, parentView.user, accepted);
            }
        }

        fun displayTrainings(trainings: List<Training>) {
            val comparator = Comparator<Training>{t1, t2 -> SDF.parse(t1.dateTime).compareTo(SDF.parse(t2.dateTime))}

            this.trainings = trainings.sortedWith(comparator)

            notifyDataSetChanged()

        }
    }
}