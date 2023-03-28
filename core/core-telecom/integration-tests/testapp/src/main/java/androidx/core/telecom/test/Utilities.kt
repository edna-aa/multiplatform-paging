/*
 * Copyright 2023 The Android Open Source Project
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

package androidx.core.telecom.test

import android.net.Uri
import androidx.annotation.RequiresApi
import androidx.core.telecom.CallAttributes
import androidx.core.telecom.CallAttributes.Companion.DIRECTION_INCOMING
import androidx.core.telecom.CallAttributes.Companion.DIRECTION_OUTGOING
import androidx.core.telecom.CallAttributes.Companion.CALL_TYPE_VIDEO_CALL

@RequiresApi(34)
class Utilities {
    companion object {
        const val APP_SCHEME = "MyCustomScheme"
        const val ALL_CALL_CAPABILITIES = (CallAttributes.SUPPORTS_SET_INACTIVE
        or CallAttributes.SUPPORTS_STREAM or CallAttributes.SUPPORTS_TRANSFER)

        // outgoing attributes constants
        const val OUTGOING_NAME = "Darth Maul"
        val OUTGOING_URI: Uri = Uri.fromParts(APP_SCHEME, "", "")
        // Define the minimal set of properties to start an outgoing call
        var OUTGOING_CALL_ATTRIBUTES = CallAttributes(
            OUTGOING_NAME,
            OUTGOING_URI,
            DIRECTION_OUTGOING)

        // incoming attributes constants
        const val INCOMING_NAME = "Sundar Pichai"
        val INCOMING_URI: Uri = Uri.fromParts(APP_SCHEME, "", "")
        // Define all possible properties for CallAttributes
        val INCOMING_CALL_ATTRIBUTES =
            CallAttributes(
                INCOMING_NAME,
                INCOMING_URI,
                DIRECTION_INCOMING,
                CALL_TYPE_VIDEO_CALL,
                ALL_CALL_CAPABILITIES)
    }
}