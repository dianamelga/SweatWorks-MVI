package com.dianascode.sweatworks.utils

import android.widget.Toast
import com.google.gson.Gson

fun String.asFormattedDate(): String {
    val milis = UtilTools.ISO8601toCalendar(this).timeInMillis
    return UtilTools.datesFromMilis(milis, "MM/dd/yyyy")
}

fun Any.toJSON(): String {
    return Gson().toJson(this)
}

fun String.isSameTextAs(tex: String): Boolean {
    return this.trim().contains(tex.trim(), true)
}
