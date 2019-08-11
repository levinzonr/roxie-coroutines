/*
* Copyright (C) 2019. WW International, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.ww.roxie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Flow

/**
 * Store which manages business data and viewState.
 */
abstract class BaseViewModel<A : BaseAction, S : BaseState> : ViewModel() {

    protected val actions: Channel<A> = Channel()

    protected abstract val initialState: S


    private val currentState: S
        get() = viewState.value ?: initialState

    protected val viewState = MutableLiveData<S>()
    private val tag by lazy { javaClass.simpleName }

    /**
     * Returns the current viewState. It is equal to the last value returned by the store's reducer.
     */
    val observableState: LiveData<S> = MediatorLiveData<S>().apply {
        addSource(viewState) { data ->
            Roxie.log("$tag: Received viewState: $data")
            setValue(data)
        }
    }

    /**
     * Dispatches an action. This is the only way to trigger a viewState change.
     */
    fun dispatch(action: A) {
        GlobalScope.launch {
            Roxie.log("$tag: Received action: $action")
            actions.send(action)

        }
        // actions.onNext(action)
    }


    override fun onCleared() {
        //  disposables.clear()
    }

    protected suspend fun<A> Channel<A>.onReceive(action: (A) -> Unit) {
        receiveOrNull()?.let(action)
    }
}