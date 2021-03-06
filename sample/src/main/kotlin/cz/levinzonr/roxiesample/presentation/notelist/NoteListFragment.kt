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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import cz.levinzonr.roxiesample.R
import cz.levinzonr.roxiesample.domain.AddNoteInteractor
import cz.levinzonr.roxiesample.domain.DeleteNoteInteractor
import cz.levinzonr.roxiesample.domain.GetNotesInteractor
import cz.levinzonr.roxiesample.domain.Note
import kotlinx.android.synthetic.main.note_list.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NoteListFragment : Fragment() {

    private val clickListener: ClickListener = this::onNoteClicked

    private val recyclerViewAdapter = NoteAdapter(clickListener)

    companion object {
        fun newInstance() = NoteListFragment()
    }
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var viewModel: NoteListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.note_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        // Normally ViewModelFactory should be injected here along with its UseCases injected into it
        viewModel = ViewModelProviders.of(
            this,
            NoteListViewModelFactory(
                null,
                GetNotesInteractor(),
                DeleteNoteInteractor(),
                AddNoteInteractor()
            )
        ).get(NoteListViewModel::class.java)

        viewModel.stateFlow.onEach { state ->
            renderState(state)
        }.launchIn(scope)

        viewModel.eventFlow.onEach {
            Toast.makeText( requireContext(), "Note Added", Toast.LENGTH_SHORT).show()
        }.launchIn(scope)

        addNoteBtn.setOnClickListener {
            viewModel.dispatch(Action.AddNote("Test"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope.cancel()
    }

    private fun renderState(state: State) {
        with(state) {
            when {
                isLoading -> renderLoadingState()
                isError -> renderErrorState()
                else -> renderNotesState(notes)
            }
        }
    }

    private fun renderLoadingState() {
        loadingIndicator.visibility = View.VISIBLE
    }

    private fun renderErrorState() {
        loadingIndicator.visibility = View.GONE
        Toast.makeText(requireContext(), R.string.error_loading_notes, Toast.LENGTH_LONG).show()
    }

    private fun renderNotesState(notes: List<Note>) {
        loadingIndicator.visibility = View.GONE
        recyclerViewAdapter.updateNotes(notes)
        notesRecyclerView.visibility = View.VISIBLE
    }

    private fun setupRecyclerView() {
        notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        notesRecyclerView.adapter = recyclerViewAdapter
        notesRecyclerView.setHasFixedSize(true)
    }

    private fun onNoteClicked(note: Note) {
        viewModel.dispatch(Action.DeleteNote(note))
    }
}
