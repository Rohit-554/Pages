package io.jadu.pages.core

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.MY_PREFS_NAME, Context.MODE_PRIVATE)

    fun setName(name: String?) {
        sharedPreferences.edit().putString(Constants.USER_NAME, name).apply()
    }

    fun getName(): String? {
        return sharedPreferences.getString(Constants.USER_NAME, null)
    }
}
