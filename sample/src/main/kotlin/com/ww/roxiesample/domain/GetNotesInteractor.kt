package com.ww.roxiesample.domain

import com.ww.roxiesample.data.NoteRepository
import kotlinx.coroutines.delay

class GetNotesInteractor : BaseAsyncInteractor<CompleteResult<List<Note>>> {


    override suspend fun invoke(): CompleteResult<List<Note>> {
        delay(3000)
        return Success(NoteRepository.loadAll())
    }
}