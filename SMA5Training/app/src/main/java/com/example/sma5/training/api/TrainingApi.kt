package com.example.sma5.training.api

import com.example.sma5.training.models.Training
import com.example.sma5.training.models.User

interface TrainingApi {
    fun insertUser(userId: String, name: String, email: String?)
    suspend fun getUserByName(name: String): User?

    fun insertOrUpdateTraining(training: Training)

    suspend fun getTraining(id: String): Training?
    suspend fun addUserToTraining(id: String, user: User, accepted: Boolean);
    suspend fun removeUserFromTraining(id: String, user: User);
}