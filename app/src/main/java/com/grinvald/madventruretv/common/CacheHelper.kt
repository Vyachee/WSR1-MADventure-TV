package com.grinvald.madventruretv.common

import android.content.Context

class CacheHelper(context: Context) {
    val prefs = context.getSharedPreferences("auth_data", Context.MODE_PRIVATE)


    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun getToken() : String{
        return prefs.getString("token", null).toString()
    }

    fun removeToken() {
        prefs.edit().remove("token").apply()
    }
}