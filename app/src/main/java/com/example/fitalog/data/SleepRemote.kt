package com.example.fitalog.data

import com.example.fitalog.model.SleepSession
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private const val SUPABASE_URL = "https://ctzndtrcxiqjalyqwkqj.supabase.co/rest/v1/"

private const val SUPABASE_ANON_KEY =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN0em5kdHJjeGlxamFseXF3a3FqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ5Nzg0ODksImV4cCI6MjA4MDU1NDQ4OX0.5upNZbo5BcfTCtH9YO8qq6y-yQrh08d6L2sf4YkkZN0"

private const val TABLE_NAME = "sleep_sessions"
private const val TABLE_PATH = TABLE_NAME

private val dateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ISO_LOCAL_DATE_TIME

data class SleepSessionDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String?,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String?,
    @SerializedName("duration") val duration: String,
    @SerializedName("score") val score: Int,
    @SerializedName("deep_sleep") val deepSleep: String,
    @SerializedName("rem_sleep") val remSleep: String,
    @SerializedName("light_sleep") val lightSleep: String,
    @SerializedName("awake_time") val awakeTime: String,
    @SerializedName("date") val date: String,
    @SerializedName("day") val day: String
)

fun SleepSessionDto.toDomain(): SleepSession =
    SleepSession(
        id = id,
        startTime = parseDateTime(startTime),
        endTime = endTime?.let { parseDateTime(it) },
        duration = duration,
        score = score,
        deepSleep = deepSleep,
        remSleep = remSleep,
        lightSleep = lightSleep,
        awakeTime = awakeTime,
        date = date,
        day = day
    )

private fun parseDateTime(dateTimeString: String): LocalDateTime {
    return try {
        OffsetDateTime.parse(dateTimeString).toLocalDateTime()
    } catch (_: Exception) {
        LocalDateTime.parse(dateTimeString, dateTimeFormatter)
    }
}

fun SleepSession.toDto(userId: String? = null): SleepSessionDto =
    SleepSessionDto(
        id = id,
        userId = userId,
        startTime = startTime.format(dateTimeFormatter),
        endTime = endTime?.format(dateTimeFormatter),
        duration = duration,
        score = score,
        deepSleep = deepSleep,
        remSleep = remSleep,
        lightSleep = lightSleep,
        awakeTime = awakeTime,
        date = date,
        day = day
    )


interface SleepApiService {

    @GET("$TABLE_PATH?select=*&order=start_time.desc&limit=20")
    suspend fun getSleepSessions(): List<SleepSessionDto>

    @POST(TABLE_PATH)
    @retrofit2.http.Headers("Prefer: return=representation")
    suspend fun insertSleepSession(
        @Body session: SleepSessionDto
    ): List<SleepSessionDto>
}

object SleepApi {

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("apikey", SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer $SUPABASE_ANON_KEY")
                .header("Content-Type", "application/json")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(SUPABASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val service: SleepApiService = retrofit.create(SleepApiService::class.java)
}
