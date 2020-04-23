package com.dianascode.sweatworks

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.dianascode.sweatworks.models.Mode
import com.dianascode.sweatworks.network.RandomUserApi
import com.dianascode.sweatworks.utils.Constants

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
class SweatWorks : MultiDexApplication() {
    var apiRandomUser: RandomUserApi?= null

    override fun onCreate() {
        super.onCreate()
        instance = this
        val url = "https://randomuser.me/"
        apiRandomUser = RandomUserApi.createForApi(url)

        val prefs = getSharedPreferences(Constants.PREFS_MODE, Context.MODE_PRIVATE)

        val nightMode = when(prefs.getInt(Constants.MODE_KEY,0)) {
            Mode.LIGHT.ordinal -> AppCompatDelegate.MODE_NIGHT_NO
            Mode.DARK.ordinal -> AppCompatDelegate.MODE_NIGHT_YES
            Mode.SYSTEM.ordinal -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            Mode.BATTERY.ordinal -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }

        AppCompatDelegate.setDefaultNightMode(nightMode)
    }


    companion object {
        private lateinit var instance: SweatWorks
        fun getContext(): Context {
            return instance
        }

        fun getInstance(): SweatWorks {
            return instance
        }
    }
}