package com.dianascode.sweatworks.modules.home

import com.dianascode.sweatworks.mvibase.MviIntent

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
sealed class HomeIntent : MviIntent {
    data class LoadUsersIntent(val results: Int): HomeIntent()
    object LoadFavoriteUsersIntent: HomeIntent()
    data class SearchUserIntent(val name: String, val favorites: Boolean): HomeIntent()
}