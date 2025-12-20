package com.example.fitalog.ui.screen.nutrinote

data class FoodItem(
    val id: Long,
    val name: String,
    val calories: Int,
    val servingText: String,
    val imageUrl: String? = null,
    val historyId: Long? = null,
    val servingCount: Int = 1
)

enum class MealType {
    SARAPAN, MAKAN_SIANG, MAKAN_MALAM, KUDAPAN
}

val WEEK_DAYS = listOf(
    "Minggu","Senin","Selasa","Rabu","Kamis","Jumat","Sabtu"
)

