package com.example.sma5.training.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var username: String? = "",
    var email: String? = "",
    var role: Roles = Roles.USER
)
