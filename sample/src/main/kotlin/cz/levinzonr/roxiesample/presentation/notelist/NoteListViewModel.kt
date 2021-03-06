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
package cz.levinzonr.roxiesample.presentation.notelist

import cz.levinzonr.roxie.RoxieViewModel
import cz.levinzonr.roxie.Reducer
import cz.levinzonr.roxiesample.domain.*
import kotlinx.coroutines.flow.*

class NoteListViewModel(
    initialState: State?,
    private val addNoteInteractor: AddNoteInteractor,
    private val deleteNoteInteractor: DeleteNoteInteractor,
    private val loadNoteListUseCase: GetNotesInteractor
) : RoxieViewModel<Action, State, Event, Change>() {

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
                notes = state.notes.toMutableList().apply { add(change.note) }
            ).withEvent(Event.NoteAdded)
        }
    }

    init {
        startActionsObserver()
        dispatch(Action.LoadNotes)
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
        runInteractor(loadNoteListUseCase)
            .isSuccess { emit(Change.Notes(it)) }
            .isError { emit(Change.Error(it)) }
    }

    private fun bindDeleteNoteInteractor(note: Note) : Flow<Change> = flow {
        emit(Change.Loading)
        deleteNoteInteractor.input = DeleteNoteInteractor.Input(note)
        runInteractor(deleteNoteInteractor)
            .isSuccess { emit(Change.NoteDeleted(it)) }
            .isError { emit(Change.Error(it)) }
    }

    private fun bindAddNoteInteractor(text: String) : Flow<Change> = flow {
        addNoteInteractor.input = AddNoteInteractor.Input(text)
        runInteractor(addNoteInteractor)
            .isSuccess { emit(Change.NoteAdded(it)) }
            .isError { emit(Change.Error(it)) }
    }
}
