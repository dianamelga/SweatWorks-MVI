package com.dianascode.sweatworks.mvibase

import io.reactivex.Observable

/*
a ViewModel must be able to process intents coming from the view and provide
a stream of states for the view to observe, so we add those functions
to the ViewModel Interface (MviViewModel)
 */
interface MviViewModel<T: MviIntent, S: MviViewState> {
    fun processIntents(intents: Observable<T>)
    fun states(): Observable<S>
}