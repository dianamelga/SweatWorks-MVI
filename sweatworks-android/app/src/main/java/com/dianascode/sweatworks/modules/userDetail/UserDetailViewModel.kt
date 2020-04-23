package com.dianascode.sweatworks.modules.userDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dianascode.sweatworks.modules.userDetail.UserDetailAction.*
import com.dianascode.sweatworks.modules.userDetail.UserDetailIntent.*
import com.dianascode.sweatworks.modules.userDetail.UserDetailResult.*
import com.dianascode.sweatworks.mvibase.MviViewModel
import com.dianascode.sweatworks.utils.toJSON
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
class UserDetailViewModel(
    private val actionProcessorHolder: UserDetailProcessorHolder
): ViewModel(), MviViewModel<UserDetailIntent, UserDetailViewState> {

    //we'll use to process intents from the view, and create state events
    //that will be observed by the view
    //intentsSubject will start our Observable stream in the viewModel
    private val intentsSubject: PublishSubject<UserDetailIntent> = PublishSubject.create()

    //the state observable value will be set up using a private method, compose
    private val statesObservable: Observable<UserDetailViewState> = compose()


    override fun processIntents(intents: Observable<UserDetailIntent>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<UserDetailViewState> = statesObservable

    private fun compose(): Observable<UserDetailViewState> {
        return intentsSubject
            .map(this::actionFromIntent)
            .compose(actionProcessorHolder.actionProcessor)
            .scan(UserDetailViewState.default(), reducer)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    private fun actionFromIntent(intent: UserDetailIntent) : UserDetailAction {
        Log.d(TAG, "Intent: ${intent.javaClass.name}")
        return when(intent){
            is AddToFavoritesIntent -> AddToFavoritesAction(intent.user)
            is RemoveFromFavoritesIntent -> RemoveFromFavoritesAction(intent.user)
            is IsFavoriteIntent -> IsFavoriteAction(intent.user)
        }
    }


    companion object {
        private const val TAG = "UserDetailViewModel"

        private val reducer = BiFunction { previousState: UserDetailViewState, result: UserDetailResult ->
            Log.d(TAG, "previousState: ${previousState.toJSON()}  ; result: ${result.toJSON()}")
            when (result) {
                is AddToFavoritesResult -> reduceAddToFavorites(previousState, result)
                is RemoveFromFavoritesResult -> reduceRemoveFromFavorites(previousState, result)
                is IsFavoriteResult -> reduceIsFavorite(previousState, result)
            }
        }


        private fun reduceAddToFavorites(
            previousState: UserDetailViewState,
            result: AddToFavoritesResult
        ): UserDetailViewState = when(result) {
            is AddToFavoritesResult.Processing -> previousState.copy(
                isProcessing = true,
                error = null
            )
            is AddToFavoritesResult.Success -> previousState.copy(
                isProcessing = false,
                error = null,
                isFavorite = true
            )
            is AddToFavoritesResult.Failure -> previousState.copy(
                isProcessing = false,
                error = result.error
            )
        }

        private fun reduceRemoveFromFavorites(
            previousState: UserDetailViewState,
            result: RemoveFromFavoritesResult
        ): UserDetailViewState = when(result) {
            is RemoveFromFavoritesResult.Processing -> previousState.copy(
                isProcessing = true,
                error = null
            )
            is RemoveFromFavoritesResult.Success -> previousState.copy(
                isProcessing = false,
                error = null,
                isFavorite = false
            )
            is RemoveFromFavoritesResult.Failure -> previousState.copy(
                isProcessing = false,
                error = result.error
            )
        }


        private fun reduceIsFavorite(
            previousState: UserDetailViewState,
            result: IsFavoriteResult
        ): UserDetailViewState = when(result) {
            is IsFavoriteResult.Processing -> previousState.copy(
                isProcessing = true,
                error = null
            )
            is IsFavoriteResult.Success -> previousState.copy(
                isProcessing = false,
                error = null,
                isFavorite = result.isFavorite
            )
            is IsFavoriteResult.Failure -> previousState.copy(
                isProcessing = false,
                error = result.error
            )
        }

    }

}