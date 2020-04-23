package com.dianascode.sweatworks.repository

import com.dianascode.sweatworks.SweatWorks
import com.dianascode.sweatworks.models.User
import com.dianascode.sweatworks.models.UserResponse
import com.dianascode.sweatworks.utils.isSameTextAs
import com.dianascode.sweatworks.utils.toJSON
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
class RandomUserRepository(private val application: SweatWorks): SweatWorksRepository(), IRandomUserRepository {
    private val users = ArrayList<User>()
    private val favoriteUsers = ArrayList<User>()

    override fun getUsers(results: Int?): Observable<UserResponse> {
        return application.apiRandomUser!!.getUsers(results).doOnNext{
            it.results?.let{u -> users.addAll(u)}
        }
    }

    override fun getFavoriteUsers(): List<User> {
        val list: ArrayList<User> = ArrayList()
        val sharedPref = application.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

        if (sharedPref.contains(PREF_NAME)) {
            val itemType = object : TypeToken<List<User>>() {}.type
            val usersList = Gson().fromJson<List<User>>(sharedPref.getString(PREF_NAME, ""), itemType)
            list.addAll(usersList)
        }
        favoriteUsers.clear()
        favoriteUsers.addAll(list)

        return list
    }

    override fun saveFavoriteUser(user: User) {
        val sharedPref = application.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val editor = sharedPref.edit()
        var usersList: ArrayList<User> = ArrayList()

        if (sharedPref.contains(PREF_NAME)) {
            val itemType = object : TypeToken<ArrayList<User>>() {}.type
            usersList = Gson().fromJson<ArrayList<User>>(sharedPref.getString(PREF_NAME, ""), itemType)
        }

        usersList.add(user)
        editor.putString(PREF_NAME, usersList.toJSON())
        editor.apply()
    }

    private fun updateFavoriteUsersList(usersList: ArrayList<User>) {
        val sharedPref = application.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        val editor = sharedPref.edit()
        editor.putString(PREF_NAME, usersList.toJSON())
        editor.apply()
    }

    override fun isFavoriteUser(user: User): Boolean {
        val users = getFavoriteUsers()
        for (u in users) {
            if (u.toJSON() == user.toJSON()) {
                return true
            }
        }
        return false
    }

    override fun removeFavoriteUser(user: User) {
        if(isFavoriteUser(user)) {
            val favorites = getFavoriteUsers()
            val favoritesNew: ArrayList<User> = ArrayList()
            favoritesNew.addAll(favorites)
            var index = 0
            for (u in favorites) {
                if(u.toJSON() == user.toJSON()) {
                    favoritesNew.removeAt(index)
                    break
                }
                index++
            }

            updateFavoriteUsersList(favoritesNew)
        }
    }

    override fun searchUser(name: String, favorites: Boolean): List<User> {
        val list = ArrayList<User>()
        if(name.isNotEmpty() && name.isNotBlank()) {
            if(!favorites) {
            for (user in users) {
                if (user.name?.first?.isSameTextAs(name) == true ||
                    user.name?.last?.isSameTextAs(name) == true ||
                    user.name?.title?.isSameTextAs(name) == true
                ) {
                    list.add(user)
                }
            }
                }else {
                for (user in favoriteUsers) {
                    if (user.name?.first?.isSameTextAs(name) == true ||
                        user.name?.last?.isSameTextAs(name) == true ||
                        user.name?.title?.isSameTextAs(name) == true
                    ) {
                        list.add(user)
                    }
                }
            }
        }else {
            if(favorites) {
                list.addAll(favoriteUsers)
            }else {
                list.addAll(users)
            }
        }
        return list
    }

    companion object {
        private const val PRIVATE_MODE = 0
        private const val PREF_NAME = "favorite-users"
    }

}