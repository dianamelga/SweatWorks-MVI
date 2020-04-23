package com.dianascode.sweatworks.modules.userDetail

import com.dianascode.sweatworks.models.User
import com.dianascode.sweatworks.mvibase.MviIntent

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
sealed class UserDetailIntent : MviIntent {
    data class AddToFavoritesIntent(val user: User): UserDetailIntent()
    data class RemoveFromFavoritesIntent(val user: User): UserDetailIntent()
    data class IsFavoriteIntent(val user: User): UserDetailIntent()
}