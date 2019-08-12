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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ww.roxiesample.domain.AddNoteInteractor
import com.ww.roxiesample.domain.DeleteNoteInteractor
import com.ww.roxiesample.domain.GetNotesInteractor

class NoteListViewModelFactory(
    private val initialState: State?,
    private val getNotesInteractor: GetNotesInteractor,
    private val deleteNoteInteractor: DeleteNoteInteractor,
    private val addNoteInteractor: AddNoteInteractor
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NoteListViewModel(initialState, addNoteInteractor, deleteNoteInteractor, getNotesInteractor) as T
    }
}