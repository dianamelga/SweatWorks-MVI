package com.dianascode.sweatworks.modules.home

import com.dianascode.sweatworks.models.User
import com.dianascode.sweatworks.mvibase.MviResult

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
sealed class HomeResult : MviResult {

    sealed class LoadUsersResult: HomeResult() {
        object Processing: LoadUsersResult()
        data class Success(val users: List<User>): LoadUsersResult()
        data class Failure(val error: Throwable): LoadUsersResult()
    }

    sealed class LoadFavoriteUsersResult: HomeResult() {
        object Processing: LoadFavoriteUsersResult()
        data class Success(val favoriteUsers: List<User>): LoadFavoriteUsersResult()
        data class Failure(val error: Throwable): LoadFavoriteUsersResult()
    }

    sealed class SearchUserResult: HomeResult() {
        object Processing: SearchUserResult()
        data class Success(val users: List<User>, val favorites: Boolean): SearchUserResult()
        data class Failure(val error: Throwable): SearchUserResult()
    }

}