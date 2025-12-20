package com.example.fitalog.model

import com.google.gson.annotations.SerializedName

data class DailyCalorieSummary(
    @SerializedName("day") val day: String,
    @SerializedName("calories_in") val caloriesIn: Double,
    @SerializedName("calories_out") val caloriesOut: Double,
    @SerializedName("net_calories") val netCalories: Double
) {
    val caloriesInInt: Int get() = caloriesIn.toInt()
    val caloriesOutInt: Int get() = caloriesOut.toInt()
    val netCaloriesInt: Int get() = netCalories.toInt()
}

