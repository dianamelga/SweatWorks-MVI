package com.dianascode.sweatworks.models

import java.io.Serializable

data class UserResponse(
    val results: List<User>?= null,
    val info: Info?= null


): Serializable

data class Info(
    val seed: String?= null,
    val results: Int?= null,
    val page: Int?= null,
    val version: String?= null
): Serializable