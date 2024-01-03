package com.example.sma5.training.models

import com.google.firebase.database.IgnoreExtraProperties
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import java.util.Locale
import java.util.UUID

var SDF = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
var zoneId = ZoneId.of("Europe/Paris")
@IgnoreExtraProperties
data class Training(
    var id: String = UUID.randomUUID().toString(),
    var title: String? = "",
    var description: String = "",
    var creator: String = "",
    var dateTime: String = SDF.format(Date.from(ZonedDateTime.now(zoneId).toInstant())),
    var acceptedUsers: List<User> = emptyList(),
    var declinedUsers: List<User> = emptyList(),
)
