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
    private val addNoteInteractor: AddNoteInteractor,
    private val deleteNoteInteractor: DeleteNoteInteractor,
    private val loadNoteListUseCase: GetNotesInteractor
) : BaseViewModel<Action, State, Change>() {

    override val initialState = initialState ?: State(isIdle = true)


    override val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.Loading -> state.copy(
                isIdle = false,
                isLoading = true,
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
            is Change.NoteDeleted -> state.copy(
                isLoading = false,
                notes = state.notes.filterNot { it.id == change.index }
            )
            is Change.NoteAdded -> state.copy(
                notes = state.notes.toMutableList().apply { add(change.note        ) }
            )
        }
    }



    override fun emitAction(action: Action): Flow<Change> {
        return when(action) {
            is Action.LoadNotes -> bindGetNotesAction()
            is Action.DeleteNote -> bindDeleteNoteInteractor(action.note)
            is Action.AddNote -> bindAddNoteInteractor(action.text)
        }
    }



    private fun bindGetNotesAction(): Flow<Change> = flow {
        emit(Change.Loading)
        when(val result = runInteractor(loadNoteListUseCase)) {
            is Success -> emit(Change.Notes(result.data))
            is Fail -> emit(Change.Error(result.throwable))
        }
    }

    private fun bindDeleteNoteInteractor(note: Note) : Flow<Change> = flow {
        emit(Change.Loading)
        deleteNoteInteractor.input = DeleteNoteInteractor.Input(note)
        when(val result = runInteractor(deleteNoteInteractor)) {
            is Success -> emit(Change.NoteDeleted(result.data))
            is Fail -> emit(Change.Error(result.throwable))
        }
    }

    private fun bindAddNoteInteractor(text: String) : Flow<Change> = flow {
        addNoteInteractor.input = AddNoteInteractor.Input(text)
        when(val result = runInteractor(addNoteInteractor)) {
            is Success -> emit(Change.NoteAdded(result.data))
            is Fail -> emit(Change.Error(result.throwable))
        }
    }
}
