package cz.levinzonr.roxiesample.presentation.notelist

import cz.levinzonr.roxie.BaseAction
import cz.levinzonr.roxie.BaseChange
import cz.levinzonr.roxie.BaseState
import cz.levinzonr.roxiesample.domain.Note


data class State(
    val notes: List<Note> = listOf(),
    val isIdle: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false
) : BaseState

sealed class Change : BaseChange {
    object Loading : Change()
    data class Notes(val notes: List<Note>) : Change()
    data class Error(val throwable: Throwable?) : Change()
    data class NoteDeleted(val index: Long) : Change()
    data class NoteAdded(val note: Note) : Change()
}

sealed class Action : BaseAction {
    object LoadNotes : Action()
    data class DeleteNote(val note: Note): Action()
    data class AddNote(val text: String) : Action()
}
