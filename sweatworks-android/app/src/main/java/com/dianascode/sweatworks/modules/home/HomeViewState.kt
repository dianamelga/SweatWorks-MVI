package com.dianascode.sweatworks.modules.home

import com.dianascode.sweatworks.models.User
import com.dianascode.sweatworks.mvibase.MviViewState

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
data class HomeViewState(
    val isProcessing: Boolean,
    val error: Throwable?,
    val users: List<User>,
    val favoriteUsers: List<User>,
    val usersMatch: List<User>,
    val favoriteUsersMatch: List<User>,
    val searching: Boolean
): MviViewState {
    companion object {
        fun default(): HomeViewState = HomeViewState(
            isProcessing = false,
            error = null,
            users = emptyList(),
            favoriteUsers = emptyList(),
            usersMatch = emptyList(),
            favoriteUsersMatch = emptyList(),
            searching = false
        )
    }
}