package com.example.fitalog.model

import com.google.gson.annotations.SerializedName

data class User(
    val uuid: String,
    val fullname: String,
    val email: String,
    val profileLink: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class UserDto(
    @SerializedName("uuid") val uuid: String? = null,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("profile_link") val profileLink: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class RegisterRequest(
    @SerializedName("fullname") val fullname: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class UserResponse(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("email") val email: String,
    @SerializedName("profile_link") val profileLink: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

fun UserResponse.toUser() = User(
    uuid = uuid,
    fullname = fullname,
    email = email,
    profileLink = profileLink,
    createdAt = createdAt,
    updatedAt = updatedAt
)

