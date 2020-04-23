package com.dianascode.sweatworks.modules.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dianascode.sweatworks.modules.home.HomeAction.*
import com.dianascode.sweatworks.modules.home.HomeIntent.*
import com.dianascode.sweatworks.modules.home.HomeResult.*
import com.dianascode.sweatworks.mvibase.MviViewModel
import com.dianascode.sweatworks.utils.toJSON
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
class HomeViewModel(
    private val actionProcessorHolder: HomeProcessorHolder
) : ViewModel(), MviViewModel<HomeIntent, HomeViewState> {

    //we'll use to process intents from the view, and create state events
    //that will be observed by the view
    //intentsSubject will start our Observable stream in the viewModel
    private val intentsSubject: PublishSubject<HomeIntent> = PublishSubject.create()

    //the state observable value will be set up using a private method, compose
    private val statesObservable: Observable<HomeViewState> = compose()


    override fun processIntents(intents: Observable<HomeIntent>) {
        intents.subscribe(intentsSubject)
    }

    override fun states(): Observable<HomeViewState> = statesObservable

    private fun compose(): Observable<HomeViewState> {
        return intentsSubject
            .map(this::actionFromIntent)
            .compose(actionProcessorHolder.actionProcessor)
            .scan(HomeViewState.default(), reducer)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    private fun actionFromIntent(intent: HomeIntent) : HomeAction {
        Log.d(TAG, "Intent: ${intent.javaClass.name}")
        return when(intent){
            is LoadUsersIntent -> LoadUsersAction(intent.results)
            is LoadFavoriteUsersIntent -> LoadFavoriteUsersAction
            is SearchUserIntent -> SearchUserAction(intent.name, intent.favorites)
        }
    }


    companion object {
        private const val TAG = "HomeViewModel"

        private val reducer = BiFunction { previousState: HomeViewState, result: HomeResult ->
            Log.d(TAG, "previousState: ${previousState.toJSON()}  ; result: ${result.toJSON()}")
            when (result) {
                is LoadUsersResult -> reduceLoadUsers(previousState, result)
                is LoadFavoriteUsersResult -> reduceLoadFavoriteUsers(previousState, result)
                is SearchUserResult -> reduceSearchUser(previousState, result)
            }
        }


        private fun reduceLoadUsers(
            previousState: HomeViewState,
            result: LoadUsersResult
        ): HomeViewState = when(result) {
            is LoadUsersResult.Processing -> previousState.copy(
                isProcessing = true,
                error = null
            )
            is LoadUsersResult.Success -> previousState.copy(
                isProcessing = false,
                error = null,
                users = result.users
            )
            is LoadUsersResult.Failure -> previousState.copy(
                isProcessing = false,
                error = result.error
            )
        }

        private fun reduceLoadFavoriteUsers(
            previousState: HomeViewState,
            result: LoadFavoriteUsersResult
        ): HomeViewState = when(result) {
            is LoadFavoriteUsersResult.Processing -> previousState.copy(
                error = null
            )
            is LoadFavoriteUsersResult.Success -> previousState.copy(
                error = null,
                favoriteUsers = result.favoriteUsers
            )
            is LoadFavoriteUsersResult.Failure -> previousState.copy(
                error = result.error
            )
        }

        private fun reduceSearchUser(
            previousState: HomeViewState,
            result: SearchUserResult
        ): HomeViewState = when(result) {
            is SearchUserResult.Processing -> previousState.copy(
            )
            is SearchUserResult.Success -> previousState.copy(
                error = null,
                usersMatch = if(!result.favorites) result.users else previousState.usersMatch,
                favoriteUsersMatch = if(result.favorites) result.users else previousState.favoriteUsersMatch,
                searching = true
            )
            is SearchUserResult.Failure -> previousState.copy(
                error = result.error,
                searching = false
            )
        }

    }

}