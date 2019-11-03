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

import cz.levinzonr.roxie.BaseAction
import cz.levinzonr.roxiesample.domain.Note

sealed class Action : BaseAction {
    object LoadNotes : Action()
    data class DeleteNote(val note: Note): Action()
    data class AddNote(val text: String) : Action()
}