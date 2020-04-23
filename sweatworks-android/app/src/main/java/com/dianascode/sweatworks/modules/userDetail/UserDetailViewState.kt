package com.dianascode.sweatworks.modules.userDetail

import com.dianascode.sweatworks.mvibase.MviViewState

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
data class UserDetailViewState(
    val isProcessing: Boolean,
    val error: Throwable?,
    val isFavorite: Boolean
): MviViewState {

    companion object {
        fun default(): UserDetailViewState = UserDetailViewState(
            isProcessing = false,
            error = null,
            isFavorite = false
        )
    }
}