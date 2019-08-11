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
package com.ww.roxiesample.presentation.notelist

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import com.ww.roxiesample.domain.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class NoteListViewModel(
    initialState: State?,
    private val loadNoteListUseCase: GetNotesInteractor
) : BaseViewModel<Action, State>() {

    override val initialState = initialState ?: State(isIdle = true)


    private val reducer: Reducer<State, Change> = { state, change ->
        Timber.d("Change: $change")
        when (change) {
            is Change.Loading -> state.copy(
                isIdle = false,
                isLoading = true,
                notes = emptyList(),
                isError = false
            )
            is Change.Notes -> state.copy(
                isLoading = false,
                notes = change.notes
            )
            is Change.Error -> state.copy(
                isLoading = false,
                isError = true
            )
        }
    }

    init {
        bindActions()
    }


    private fun bindActions() = GlobalScope.launch {
        actions.consumeEach {
            when (it) {
                Action.LoadNotes -> bindGetNotesAction()
                    .scan(initialState, reducer)
                    .also { "After scan" }
                    .filter { !it.isIdle }
                    .distinctUntilChanged()
                    .collect { viewState.postValue(it) }
            }
        }

    }


    private fun bindGetNotesAction(): Flow<Change> = flow {
        emit(Change.Loading)
        when(val result = runInteractor(loadNoteListUseCase)) {
            is Success -> emit(Change.Notes(result.data))
            is Fail -> emit(Change.Error(result.throwable))
        }
    }
}
