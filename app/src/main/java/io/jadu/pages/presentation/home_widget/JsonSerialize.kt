package io.jadu.pages.presentation.home_widget

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson = Gson()

fun <T> T.toJson(): String {
    return gson.toJson(this)
}

inline fun <reified T> String.fromJson(): T? {
    return try {
        gson.fromJson(this, object : TypeToken<T>() {}.type)
    } catch (e: Exception) {
        null
    }
}
