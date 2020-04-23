package com.dianascode.sweatworks.mvibase

import io.reactivex.Observable

/*
the view must provide intents for the ViewModel and also be able to render new state
coming from the viewModel
 */
interface MviView<I: MviIntent, in S: MviViewState> {
    fun intents(): Observable<I>
    fun render(state: S)
}