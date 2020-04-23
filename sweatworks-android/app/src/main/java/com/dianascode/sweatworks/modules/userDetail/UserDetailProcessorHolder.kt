package com.dianascode.sweatworks.modules.userDetail

import com.dianascode.sweatworks.modules.userDetail.UserDetailAction.*
import com.dianascode.sweatworks.modules.userDetail.UserDetailResult.*
import com.dianascode.sweatworks.repository.RandomUserRepository
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import py.com.bancop.app.utils.schedulers.BaseSchedulerProvider

/**
 * Created by Diana Melgarejo on 4/22/20.
 */
class UserDetailProcessorHolder (
    private val repository: RandomUserRepository,
    private val schedulerProvider: BaseSchedulerProvider
) {

    private val addToFavoritesActionProcessor =
        ObservableTransformer<AddToFavoritesAction, AddToFavoritesResult> { actions ->
            actions.flatMap {action ->
                Observable.just(repository.saveFavoriteUser(action.user))
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .map{ AddToFavoritesResult.Success}
                    .cast(AddToFavoritesResult::class.java)
                    .onErrorReturn(AddToFavoritesResult::Failure)
                    .startWith(AddToFavoritesResult.Processing)
            }
        }

    private val removeFromFavoritesActionProcessor =
        ObservableTransformer<RemoveFromFavoritesAction, RemoveFromFavoritesResult> { actions ->
            actions.flatMap { action ->
                repository.removeFavoriteUser(action.user)
                Observable.just(RemoveFromFavoritesResult.Success)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .map { RemoveFromFavoritesResult.Success }
                    .cast(RemoveFromFavoritesResult::class.java)
                    .onErrorReturn(RemoveFromFavoritesResult::Failure)
                    .startWith(RemoveFromFavoritesResult.Processing)
            }
        }

    private val isFavoriteActionProcessor =
        ObservableTransformer<IsFavoriteAction, IsFavoriteResult> { actions ->
            actions.flatMap { action ->
                Observable.just(repository.isFavoriteUser(action.user))
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .map { response -> IsFavoriteResult.Success(response) }
                    .cast(IsFavoriteResult::class.java)
                    .onErrorReturn(IsFavoriteResult::Failure)
                    .startWith(IsFavoriteResult.Processing)

            }
        }


    internal var actionProcessor =
        ObservableTransformer<UserDetailAction, UserDetailResult> { actions ->
            actions.publish { shared ->
                Observable.mergeArray(
                    shared.ofType(AddToFavoritesAction::class.java).compose(addToFavoritesActionProcessor),
                    shared.ofType(RemoveFromFavoritesAction::class.java).compose(removeFromFavoritesActionProcessor),
                    shared.ofType(IsFavoriteAction::class.java).compose(isFavoriteActionProcessor),
                    shared.filter { v ->
                        v !is AddToFavoritesAction &&
                                v !is RemoveFromFavoritesAction &&
                                v !is IsFavoriteAction
                    }.flatMap { w ->
                        Observable.error<UserDetailResult>(
                            IllegalArgumentException("Unknown action type $w")
                        )
                    }
                )
            }
        }
}