package com.example.sma5.training.api

import android.util.Log
import com.example.sma5.training.models.Training
import com.example.sma5.training.models.User
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

object TrainingApiFactory {
    fun getApi(): TrainingApi =
        TrainingApiImplementation()
}

private class TrainingApiImplementation() : TrainingApi {
    private val TAG = "TrainingApiImplementation"
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
        val snapshot = database.child(usersKey).get().await()

        for(postSnapshot in snapshot.children) {
            val user = postSnapshot.getValue(User::class.java)
            if(user!!.username!! == name) {
                result = user
                break
            }
        }

        return result;
    }

    override suspend fun getTraining(id: String): Training? {
        val snapshot = database.child(trainingsKey).child(id)
            .get()
            .await()

        if(!snapshot.hasChildren()) return null;
        return snapshot.getValue(Training::class.java);
    }

    override suspend fun addUserToTraining(id: String, user: User, accepted: Boolean) {
        val training = getTraining(id);
        if(training == null) {
            Log.w(TAG, "No Training available")
        }

        if(accepted && training!!.acceptedUsers.contains(user.email)) return;
        if(!accepted && training!!.declinedUsers.contains(user.email)) return;

        if(accepted) {
            training!!.declinedUsers = training.declinedUsers.minus(user.email!!);
            training!!.acceptedUsers = training.acceptedUsers.plus(user.email!!);
        } else {
            training!!.acceptedUsers = training.acceptedUsers.minus(user.email!!);
            training!!.declinedUsers = training.declinedUsers.plus(user.email!!);
        }
        insertOrUpdateTraining(training);
    }

    override suspend fun removeUserFromTraining(id: String, user: User) {
        val training = getTraining(id);
        if(training == null) {
            Log.w(TAG, "No Training available")
        }

        if(!training!!.acceptedUsers.contains(user.username) &&
            !training.declinedUsers.contains(user.username)) return;

        if(training.acceptedUsers.contains(user.username)) {
            training.acceptedUsers.minus(user.username);
        } else {
            training.declinedUsers.minus(user.username);
        }
        insertOrUpdateTraining(training);
    }

    override fun insertOrUpdateTraining(training: Training) {
        database.child(trainingsKey).child(training.id).setValue(training)
    }


}