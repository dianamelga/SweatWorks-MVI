package com.dianascode.sweatworks.modules.home

import com.dianascode.sweatworks.modules.home.HomeAction.*
import com.dianascode.sweatworks.modules.home.HomeResult.*
import com.dianascode.sweatworks.repository.RandomUserRepository
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import py.com.bancop.app.utils.schedulers.BaseSchedulerProvider
import java.lang.IllegalArgumentException

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
class HomeProcessorHolder constructor(
    private val repository: RandomUserRepository,
    private val schedulerProvider: BaseSchedulerProvider
) {

    private val loadUsersActionProcessor =
        ObservableTransformer<LoadUsersAction, LoadUsersResult> { actions ->
            actions.flatMap { action ->
                repository.getUsers(action.results)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .map { response -> LoadUsersResult.Success(response.results ?: emptyList()) }
                    .cast(LoadUsersResult::class.java)
                    .onErrorReturn(LoadUsersResult::Failure)
                    .startWith(LoadUsersResult.Processing)
            }
        }


    private val loadFavoriteUsersActionProcessor =
        ObservableTransformer<LoadFavoriteUsersAction, LoadFavoriteUsersResult> { actions ->
            actions.flatMap {
                Observable.just(repository.getFavoriteUsers())
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .map { response -> LoadFavoriteUsersResult.Success(response)}
                    .cast(LoadFavoriteUsersResult::class.java)
                    .onErrorReturn(LoadFavoriteUsersResult::Failure)
                    .startWith(LoadFavoriteUsersResult.Processing)
            }
        }


    private val searchUserActionProcessor =
        ObservableTransformer<SearchUserAction, SearchUserResult> { actions ->
            actions.flatMap { action ->
                Observable.just(repository.searchUser(action.name, action.favorites))
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .map { response -> SearchUserResult.Success(response, action.favorites)}
                    .cast(SearchUserResult::class.java)
                    .onErrorReturn(SearchUserResult::Failure)
                    .startWith(SearchUserResult.Processing)
            }
        }


    internal var actionProcessor =
        ObservableTransformer<HomeAction, HomeResult> { actions ->
            actions.publish{ shared ->
                Observable.mergeArray(
                    shared.ofType(LoadUsersAction::class.java).compose(loadUsersActionProcessor),
                    shared.ofType(LoadFavoriteUsersAction::class.java).compose(loadFavoriteUsersActionProcessor),
                    shared.ofType(SearchUserAction::class.java).compose(searchUserActionProcessor),
                    shared.filter { v ->
                        v !is LoadUsersAction &&
                                v !is LoadFavoriteUsersAction &&
                                v !is SearchUserAction
                    }.flatMap { w ->
                        Observable.error<HomeResult>(
                            IllegalArgumentException("Unknown action type $w")
                        )
                    }
                )
            }
        }
}