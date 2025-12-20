package com.example.fitalog.api

import com.example.fitalog.model.WorkoutLog
import com.example.fitalog.model.FoodInsertRequest
import com.example.fitalog.model.FoodResponse
import com.example.fitalog.model.FoodHistoryInsertRequest
import com.example.fitalog.model.FoodHistoryResponse
import com.example.fitalog.model.FoodHistoryWithDetails
import com.example.fitalog.model.RegisterRequest
import com.example.fitalog.model.UserResponse
import com.example.fitalog.model.DailyCalorieSummary
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("users")
    @Headers("Prefer: return=representation", "Content-Type: application/json")
    suspend fun registerUser(@Body request: RegisterRequest): List<UserResponse>

    @GET("users")
    @Headers("Prefer: return=representation")
    suspend fun getUserByEmail(
        @Query("email") emailFilter: String,
        @Query("select") select: String = "*"
    ): List<UserResponse>

    @GET("users")
    @Headers("Prefer: return=representation")
    suspend fun getUserByUuid(
        @Query("uuid") uuidFilter: String,
        @Query("select") select: String = "*"
    ): List<UserResponse>

    @GET("workout_logs")
    suspend fun getWorkoutLogs(
        @Header("Prefer") prefer: String = "return=representation"
    ): List<WorkoutLog>

    @POST("workout_logs")
    suspend fun insertWorkoutLog(
        @Body workoutLog: WorkoutLog,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<WorkoutLog>

    @DELETE("workout_logs")
    suspend fun deleteWorkoutLog(
        @Query("timestamp") timestampFilter: String,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<Unit>

    @GET("nutrinote_input")
    @Headers("Prefer: return=representation")
    suspend fun getFoods(
        @Query("order") order: String = "created_at.desc"
    ): List<FoodResponse>

    @POST("nutrinote_input")
    @Headers("Prefer: return=representation", "Content-Type: application/json")
    suspend fun insertFood(@Body request: FoodInsertRequest): List<FoodResponse>

    @GET("nutrinote_food_history")
    @Headers("Prefer: return=representation")
    suspend fun getFoodHistory(
        @Query("eaten_at") eatenAtFilter: String = "",
        @Query("order") order: String = "eaten_at.desc"
    ): List<FoodHistoryResponse>

    @GET("nutrinote_food_history")
    @Headers("Prefer: return=representation")
    suspend fun getFoodHistoryWithDetails(
        @Query("select") select: String = "*,nutrinote_input(*)",
        @Query("order") order: String = "eaten_at.desc"
    ): List<FoodHistoryWithDetails>

    @POST("nutrinote_food_history")
    @Headers("Prefer: return=representation", "Content-Type: application/json")
    suspend fun insertFoodHistory(@Body request: FoodHistoryInsertRequest): List<FoodHistoryResponse>

    @DELETE("nutrinote_food_history")
    @Headers("Prefer: return=representation")
    suspend fun deleteFoodHistory(
        @Query("id") id: String
    ): List<FoodHistoryResponse>

    @GET("daily_calorie_summary")
    @Headers("Prefer: return=representation")
    suspend fun getDailyCalorieSummary(
        @Query("order") order: String = "day.desc",
        @Query("limit") limit: Int = 7
    ): List<DailyCalorieSummary>
}
