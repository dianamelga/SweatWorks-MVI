package com.dianascode.sweatworks.modules.userDetail

import com.dianascode.sweatworks.models.User
import com.dianascode.sweatworks.mvibase.MviResult

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
sealed class UserDetailResult : MviResult {

    sealed class AddToFavoritesResult: UserDetailResult() {
        object Processing: AddToFavoritesResult()
        object Success: AddToFavoritesResult()
        data class Failure(val error: Throwable): AddToFavoritesResult()
    }

    sealed class RemoveFromFavoritesResult: UserDetailResult() {
        object Processing: RemoveFromFavoritesResult()
        object Success: RemoveFromFavoritesResult()
        data class Failure(val error: Throwable): RemoveFromFavoritesResult()
    }

    sealed class IsFavoriteResult: UserDetailResult() {
        object Processing: IsFavoriteResult()
        data class Success(val isFavorite: Boolean): IsFavoriteResult()
        data class Failure(val error: Throwable): IsFavoriteResult()
    }

}