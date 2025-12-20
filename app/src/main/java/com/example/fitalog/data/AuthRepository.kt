package com.example.fitalog.data

import android.content.Context
import android.content.SharedPreferences
import com.example.fitalog.api.RetrofitClient
import com.example.fitalog.model.RegisterRequest
import com.example.fitalog.model.User
import com.example.fitalog.model.toUser
import java.security.MessageDigest

class AuthRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val apiService = RetrofitClient.apiService

    companion object {
        private const val PREF_NAME = "auth_prefs"
        private const val KEY_USER_UUID = "user_uuid"
        private const val KEY_USER_FULLNAME = "user_fullname"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PROFILE_LINK = "user_profile_link"
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    suspend fun register(fullname: String, email: String, password: String): Result<User> {
        return try {
            // Check if email already exists
            val existingUsers = apiService.getUserByEmail("eq.$email")
            if (existingUsers.isNotEmpty()) {
                return Result.failure(Exception("Email sudah terdaftar"))
            }

            val hashedPassword = hashPassword(password)

            val request = RegisterRequest(
                fullname = fullname,
                email = email,
                password = hashedPassword
            )

            val response = apiService.registerUser(request)
            if (response.isEmpty()) {
                return Result.failure(Exception("Gagal membuat akun"))
            }

            val user = response.first().toUser()

            saveUserSession(user)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val hashedPassword = hashPassword(password)
            val response = apiService.getUserByEmail("eq.$email")
            if (response.isEmpty()) {
                return Result.failure(Exception("Email atau password salah"))
            }

            val userResponse = response.first()
            val user = userResponse.toUser()
            saveUserSession(user)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("Login gagal: ${e.message}"))
        }
    }

    private fun saveUserSession(user: User) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_UUID, user.uuid)
            putString(KEY_USER_FULLNAME, user.fullname)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_PROFILE_LINK, user.profileLink)
            apply()
        }
    }

    fun getCurrentUser(): User? {
        val uuid = sharedPreferences.getString(KEY_USER_UUID, null) ?: return null
        val fullname = sharedPreferences.getString(KEY_USER_FULLNAME, null) ?: return null
        val email = sharedPreferences.getString(KEY_USER_EMAIL, null) ?: return null
        val profileLink = sharedPreferences.getString(KEY_USER_PROFILE_LINK, null)

        return User(
            uuid = uuid,
            fullname = fullname,
            email = email,
            profileLink = profileLink
        )
    }

    suspend fun refreshUserData(uuid: String): Result<User> {
        return try {
            val response = apiService.getUserByUuid("eq.$uuid")
            if (response.isEmpty()) {
                return Result.failure(Exception("User tidak ditemukan"))
            }

            val user = response.first().toUser()
            saveUserSession(user)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getString(KEY_USER_UUID, null) != null
    }
}

