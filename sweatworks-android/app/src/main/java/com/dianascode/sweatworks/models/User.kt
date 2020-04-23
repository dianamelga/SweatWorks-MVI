package com.dianascode.sweatworks.models

import java.io.Serializable

data class User(
    val gender: String?= null,
    val name: Name?= null,
    val location: Location?= null,
    val email: String?= null,
    val login: Login?= null,
    val dob: DateOfBirth?= null,
    val registered: Register?= null,
    val phone: String?= null,
    val cell: String?= null,
    val id: Id?= null,
    val picture: UserPicture?= null,
    val nat: String?= null
): Serializable

data class Name(
    val title: String?= null,
    val first: String?= null,
    val last: String?= null
): Serializable

data class Location(
    val street: Street?= null,
    val city: String?= null,
    val state: String?= null,
    val postcode: String?= null,
    val coordinates: Coordinates?= null,
    val timezone: TimeZone?= null
): Serializable


data class Street(
    val number: Int?=null,
    val name: String?=null
): Serializable

data class Coordinates(
    val latitude: String?= null,
    val longitude: String?= null
): Serializable

data class TimeZone(
    val offset: String?= null,
    val description: String?= null
): Serializable

data class Login(
    val uuid: String?= null,
    val username: String?= null,
    val password: String?= null,
    val salt: String?= null,
    val md5: String?= null,
    val sha1: String?= null,
    val sha256: String?= null

): Serializable

data class DateOfBirth(
    val date: String?=null,
    val age: Int?=null
): Serializable

data class Register(
    val date: String?=null,
    val age: Int?=null
): Serializable

data class Id(
    val name: String?= null,
    val value: String?= null
): Serializable

data class UserPicture(
    val large: String?= null,
    val medium: String?= null,
    val thumbnail: String?= null
): Serializable