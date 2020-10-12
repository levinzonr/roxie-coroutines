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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import cz.levinzonr.roxiesample.domain.GetNotesInteractor
import cz.levinzonr.roxiesample.domain.Note
import cz.levinzonr.roxiesample.domain.Success
import cz.levinzonr.roxiesample.presentation.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NoteListViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var testSubject: NoteListViewModel

    private val idleState = State(isIdle = true)

    private val loadingState = State(isLoading = true)

    private val noteListUseCase = mockk<GetNotesInteractor>()

    private val observer = mockk<Observer<State>>(relaxUnitFun = true)

    @Before
    fun setUp() {
        coEvery { noteListUseCase.invoke() } returns Success(listOf())
        testSubject = NoteListViewModel(idleState, mockk(), mockk(), noteListUseCase)
        testSubject.observableState.observeForever(observer)
    }

    @Test
    fun `Given notes successfully loaded, when action LoadNotes is received, then State contains notes`() {
        // GIVEN
        val noteList = listOf(Note(1L, "dummy text"))
        val successState = State(noteList)

        coEvery { noteListUseCase.invoke() } returns Success(noteList)

        // WHEN
        testSubject.dispatch(Action.LoadNotes)

        // THEN
        verifyOrder {
            observer.onChanged(loadingState)
            observer.onChanged(successState)
        }
    }


}