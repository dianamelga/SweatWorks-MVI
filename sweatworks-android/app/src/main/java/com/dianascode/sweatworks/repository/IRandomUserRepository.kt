package com.dianascode.sweatworks.repository

import com.dianascode.sweatworks.network.RandomUserApi
import com.dianascode.sweatworks.models.User

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
interface IRandomUserRepository : RandomUserApi {
    fun getFavoriteUsers(): List<User>
    fun saveFavoriteUser(user: User)
    fun isFavoriteUser(user: User): Boolean
    fun removeFavoriteUser( user: User)
    fun searchUser(name: String, favorites: Boolean): List<User>

}