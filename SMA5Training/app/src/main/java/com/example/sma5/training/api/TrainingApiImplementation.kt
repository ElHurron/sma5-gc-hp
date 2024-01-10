package com.example.sma5.training.api

import android.util.Log
import com.example.sma5.training.models.Training
import com.example.sma5.training.models.User
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.snapshots
import com.google.firebase.database.values
import kotlinx.coroutines.tasks.await

object TrainingApiFactory {
    fun getApi(): TrainingApi =
        TrainingApiImplementation()
}

private class TrainingApiImplementation() : TrainingApi {
    private val usersKey: String = "users"
    private val trainingsKey: String = "trainings"
    private val database: DatabaseReference = Firebase.database.reference

    init {

    }

    override fun insertUser(userId: String, name: String, email: String?) {
        val user = User(name, email)
        database.child(usersKey).child(userId).setValue(user)
    }

    override suspend fun getUserByName(name: String): User? {
        var result: User? = null
        var snapshot = database.child(usersKey).get().await()

        for(postSnapshot in snapshot.children) {
            var user = postSnapshot.getValue(User::class.java)
            if(user!!.username!! == name) {
                result = user
                break
            }
        }

        return result;
    }

    override fun insertTraining(training: Training) {
        database.child(trainingsKey).child(training.id).setValue(training)
    }
}