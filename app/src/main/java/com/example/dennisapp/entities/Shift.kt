package com.example.dennisapp.entities

import java.util.Date

data class Shift(
    var shiftDate: Date = Date(),
    val startTime: String = "",
    var endTime: String = "",
)