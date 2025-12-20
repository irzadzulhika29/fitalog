package com.example.fitalog.model

import com.google.gson.annotations.SerializedName

data class FoodInsertRequest(
    val name: String,
    val calories: Int,
    @SerializedName("serving_text")
    val servingText: String,
    @SerializedName("image_url")
    val imageUrl: String
)

data class FoodResponse(
    val id: Long,
    val name: String,
    val calories: Int,
    @SerializedName("serving_text")
    val servingText: String,
    @SerializedName("image_url")
    val imageUrl: String?
)

data class FoodHistoryInsertRequest(
    @SerializedName("food_id")
    val foodId: Long,
    @SerializedName("serving_count")
    val servingCount: Int = 1,
    val calories: Long,
    @SerializedName("meal_type")
    val mealType: String,
    @SerializedName("eaten_at")
    val eatenAt: String // ISO 8601 timestamp
)

data class FoodHistoryResponse(
    val id: Long,
    @SerializedName("food_id")
    val foodId: Long,
    @SerializedName("serving_count")
    val servingCount: Int,
    val calories: Long,
    @SerializedName("meal_type")
    val mealType: String,
    @SerializedName("eaten_at")
    val eatenAt: String // ISO 8601 timestamp
)

data class FoodHistoryWithDetails(
    val id: Long,
    @SerializedName("food_id")
    val foodId: Long,
    @SerializedName("serving_count")
    val servingCount: Int,
    val calories: Long,
    @SerializedName("meal_type")
    val mealType: String,
    @SerializedName("eaten_at")
    val eatenAt: String,
    // Joined from nutrinote_input
    val nutrinote_input: FoodResponse?
)
