package com.dianascode.sweatworks.modules.userDetail

import com.dianascode.sweatworks.models.User
import com.dianascode.sweatworks.mvibase.MviAction

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
sealed class UserDetailAction : MviAction {
    data class AddToFavoritesAction(val user: User): UserDetailAction()
    data class RemoveFromFavoritesAction(val user: User): UserDetailAction()
    data class IsFavoriteAction(val user: User): UserDetailAction()
}