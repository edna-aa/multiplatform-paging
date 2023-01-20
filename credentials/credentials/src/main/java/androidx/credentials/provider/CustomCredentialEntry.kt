/*
 * Copyright 2022 The Android Open Source Project
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
package androidx.credentials.provider

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.slice.Slice
import android.app.slice.SliceSpec
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import java.time.Instant
import java.util.Collections

/**
 * Base class for a generic credential entry that is displayed on the account selector UI.
 * Each entry corresponds to an account that can provide a credential.
 *
 * @property title the title shown with this entry on the selector UI
 * @property subTitle the subTitle shown with this entry on the selector UI
 * @property lastUsedTime the last used time the credential underlying this entry was
 * used by the user
 * @property icon the icon to be displayed with this entry on the selector UI
 * @property pendingIntent the [PendingIntent] to be invoked when this entry
 * is selected by the user
 * @property typeDisplayName the friendly name to be displayed on the UI for
 * the type of the credential
 * @property isAutoSelectAllowed whether this entry is allowed to be auto
 * selected if it is the only one on the UI. Note that setting this value
 * to true does not guarantee this behavior. The developer must also set this
 * to true, and the framework must determine that only one entry is present.
 */
@RequiresApi(34)
class CustomCredentialEntry internal constructor(
    type: String,
    val title: CharSequence,
    val subTitle: CharSequence?,
    val typeDisplayName: CharSequence?,
    val icon: Icon?,
    val lastUsedTime: Instant?,
    val pendingIntent: PendingIntent,
    val isAutoSelectAllowed: Boolean
    ) : android.service.credentials.CredentialEntry(
    type,
    toSlice(
        type,
        title,
        subTitle,
        pendingIntent,
        typeDisplayName,
        lastUsedTime,
        icon,
        isAutoSelectAllowed
    )
) {
    init {
        require(type.isNotEmpty()) { "type must not be empty" }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(@NonNull dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
    }

    @Suppress("AcronymName")
    @RequiresApi(34)
    companion object {
        private const val TAG = "CredentialEntry"
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val SLICE_HINT_TYPE_DISPLAY_NAME =
            "androidx.credentials.provider.credentialEntry.SLICE_HINT_TYPE_DISPLAY_NAME"
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val SLICE_HINT_TITLE =
            "androidx.credentials.provider.credentialEntry.SLICE_HINT_USER_NAME"
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val SLICE_HINT_SUBTITLE =
            "androidx.credentials.provider.credentialEntry.SLICE_HINT_CREDENTIAL_TYPE_DISPLAY_NAME"
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val SLICE_HINT_LAST_USED_TIME_MILLIS =
            "androidx.credentials.provider.credentialEntry.SLICE_HINT_LAST_USED_TIME_MILLIS"
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val SLICE_HINT_ICON =
            "androidx.credentials.provider.credentialEntry.SLICE_HINT_PROFILE_ICON"
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val SLICE_HINT_PENDING_INTENT =
            "androidx.credentials.provider.credentialEntry.SLICE_HINT_PENDING_INTENT"
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val SLICE_HINT_AUTO_ALLOWED =
            "androidx.credentials.provider.credentialEntry.SLICE_HINT_AUTO_ALLOWED"
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val AUTO_SELECT_TRUE_STRING = "true"
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val AUTO_SELECT_FALSE_STRING = "false"

        /** @hide */
        @JvmStatic
        internal fun toSlice(
            type: String,
            title: CharSequence,
            subTitle: CharSequence?,
            pendingIntent: PendingIntent,
            typeDisplayName: CharSequence?,
            lastUsedTime: Instant?,
            icon: Icon?,
            isAutoSelectAllowed: Boolean
        ): Slice {
            // TODO("Put the right revision value")
            val autoSelectAllowed = if (isAutoSelectAllowed) {
                AUTO_SELECT_TRUE_STRING
            } else {
                AUTO_SELECT_FALSE_STRING
            }
            val sliceBuilder = Slice.Builder(Uri.EMPTY, SliceSpec(
                type, 1))
                .addText(typeDisplayName, /*subType=*/null,
                    listOf(SLICE_HINT_TYPE_DISPLAY_NAME))
                .addText(title, /*subType=*/null,
                    listOf(SLICE_HINT_TITLE))
                .addText(subTitle, /*subType=*/null,
                    listOf(SLICE_HINT_SUBTITLE))
                .addText(autoSelectAllowed, /*subType=*/null,
                    listOf(SLICE_HINT_AUTO_ALLOWED))
            if (lastUsedTime != null) {
                sliceBuilder.addLong(lastUsedTime.toEpochMilli(),
                    /*subType=*/null,
                    listOf(SLICE_HINT_LAST_USED_TIME_MILLIS))
            }
            if (icon != null) {
                sliceBuilder.addIcon(icon, /*subType=*/null,
                    listOf(SLICE_HINT_ICON))
            }
            sliceBuilder.addAction(pendingIntent,
                Slice.Builder(sliceBuilder)
                    .addHints(Collections.singletonList(SLICE_HINT_PENDING_INTENT))
                    .build(),
                /*subType=*/null)
            return sliceBuilder.build()
        }

        /**
         * Returns an instance of [CustomCredentialEntry] derived from a [Slice] object.
         *
         * @param slice the [Slice] object constructed through [toSlice]
         *
         * @hide
         */
        @SuppressLint("WrongConstant") // custom conversion between jetpack and framework
        @JvmStatic
        fun fromSlice(slice: Slice): CustomCredentialEntry? {
            var typeDisplayName: CharSequence? = null
            var title: CharSequence? = null
            var subTitle: CharSequence? = null
            var icon: Icon? = null
            var pendingIntent: PendingIntent? = null
            var lastUsedTime: Instant? = null
            var autoSelectAllowed = false

            slice.items.forEach {
                if (it.hasHint(SLICE_HINT_TYPE_DISPLAY_NAME)) {
                    typeDisplayName = it.text
                } else if (it.hasHint(SLICE_HINT_TITLE)) {
                    title = it.text
                } else if (it.hasHint(SLICE_HINT_SUBTITLE)) {
                    subTitle = it.text
                } else if (it.hasHint(SLICE_HINT_ICON)) {
                    icon = it.icon
                } else if (it.hasHint(SLICE_HINT_PENDING_INTENT)) {
                    pendingIntent = it.action
                } else if (it.hasHint(SLICE_HINT_LAST_USED_TIME_MILLIS)) {
                    lastUsedTime = Instant.ofEpochMilli(it.long)
                } else if (it.hasHint(SLICE_HINT_AUTO_ALLOWED)) {
                    val autoSelectValue = it.text
                    if (autoSelectValue == AUTO_SELECT_TRUE_STRING) {
                        autoSelectAllowed = true
                    }
                }
            }

            return try {
                CustomCredentialEntry(slice.spec!!.type, title!!,
                    subTitle,
                    typeDisplayName!!,
                    icon,
                    lastUsedTime, pendingIntent!!, autoSelectAllowed)
            } catch (e: Exception) {
                Log.i(TAG, "fromSlice failed with: " + e.message)
                null
            }
        }

        @JvmField val CREATOR: Parcelable.Creator<CustomCredentialEntry> = object :
            Parcelable.Creator<CustomCredentialEntry> {
            override fun createFromParcel(p0: Parcel?): CustomCredentialEntry? {
                val baseEntry =
                    android.service.credentials.CredentialEntry.CREATOR.createFromParcel(p0)
                return fromSlice(baseEntry.slice)
            }

            @Suppress("ArrayReturn")
            override fun newArray(size: Int): Array<CustomCredentialEntry?> {
                return arrayOfNulls(size)
            }
        }
    }
}
