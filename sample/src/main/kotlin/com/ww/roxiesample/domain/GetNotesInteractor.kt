package com.ww.roxiesample.domain

import com.ww.roxiesample.data.NoteRepository
import kotlinx.coroutines.delay

class GetNotesInteractor : BaseAsyncInteractor<CompleteResult<List<Note>>> {


    override suspend fun invoke(): CompleteResult<List<Note>> {
        delay(3000)
        return Success(NoteRepository.loadAll())
    }
}

class DeleteNoteInteractor() : BaseAsyncInteractor<CompleteResult<Long>> {
    data class Input(val note: Note)
    var input: Input? = null
    override suspend fun invoke(): CompleteResult<Long> {
        NoteRepository.delete(requireNotNull(input?.note))
        return Success(requireNotNull(input?.note?.id))
    }
}


class AddNoteInteractor() : BaseAsyncInteractor<CompleteResult<Note>> {
    data class Input(val note: String)
    var input: Input? = null
    override suspend fun invoke(): CompleteResult<Note> {
        val note = NoteRepository.addNote(requireNotNull(input?.note))
        return Success(note)
    }
}