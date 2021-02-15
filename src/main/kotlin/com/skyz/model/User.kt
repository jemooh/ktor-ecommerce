package com.skyz.model

import io.ktor.auth.*

data class User(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val username: String,
    val password: String
) : Principal

data class PostUserBody(
    val first_name: String,
    val last_name: String,
    val username: String,
    val password: String
) : Principal


data class PutUserBody(
    val first_name: String,
    val last_name: String,
    val username: String
) : Principal