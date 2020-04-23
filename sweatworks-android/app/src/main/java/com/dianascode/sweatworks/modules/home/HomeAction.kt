package com.dianascode.sweatworks.modules.home

import com.dianascode.sweatworks.mvibase.MviAction

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
sealed class HomeAction : MviAction {
    data class LoadUsersAction(val results: Int): HomeAction()
    object LoadFavoriteUsersAction: HomeAction()
    data class SearchUserAction(val name: String, val favorites: Boolean): HomeAction()
}