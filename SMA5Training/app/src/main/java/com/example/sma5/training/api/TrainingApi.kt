package com.example.sma5.training.api

import com.example.sma5.training.models.Training
import com.example.sma5.training.models.User

interface TrainingApi {
    fun insertUser(userId: String, name: String, email: String?)
    suspend fun getUserByName(name: String): User?

    fun insertTraining(training: Training)
}