/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.foundation.text

import androidx.compose.foundation.text.selection.SelectionManager
import androidx.compose.foundation.text.selection.TextFieldSelectionManager
import androidx.compose.runtime.Composable

// TODO (b/269341173) remove inline once these composables are non-trivial

@Composable
internal actual inline fun ContextMenuArea(
    manager: TextFieldSelectionManager,
    content: @Composable () -> Unit
) {
    content()
}

@Composable
internal actual inline fun ContextMenuArea(
    manager: SelectionManager,
    content: @Composable () -> Unit
) {
    content()
}
