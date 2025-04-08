package com.example.dacs3.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String = "",
    val username: String,
    val email: String,
    val password: String = "", // Only used temporarily during registration
    val name: String = "",
    val age: String = "",
    val phone: String = "",
    val interests: String = "",
    val city: String = "",
    val profilePicture: String = ""
)