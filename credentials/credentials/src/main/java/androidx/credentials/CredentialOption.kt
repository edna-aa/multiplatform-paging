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

package androidx.credentials

import android.content.ComponentName
import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting
import androidx.credentials.internal.FrameworkClassParsingException

/**
 * Base class for getting a specific type of credentials.
 *
 * [GetCredentialRequest] will be composed of a list of [CredentialOption] subclasses to indicate
 * the specific credential types and configurations that your app accepts.
 *
 * @property allowedProviders a set of provider service [ComponentName] allowed to receive this
 * option. This property will only be honored at API level >= 34; also a [SecurityException] will
 * be thrown if it is set as non-empty but your app does not have
 * android.permission.CREDENTIAL_MANAGER_SET_ALLOWED_PROVIDERS. For API level < 34, control the
 * allowed provider via [library dependencies](https://developer.android.com/training/sign-in/passkeys#add-dependencies).
 */
abstract class CredentialOption internal constructor(
    /** @hide */
    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val type: String,
    /** @hide */
    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val requestData: Bundle,
    /** @hide */
    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val candidateQueryData: Bundle,
    /** @hide */
    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val isSystemProviderRequired: Boolean,
    /** @hide */
    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val isAutoSelectAllowed: Boolean,
    val allowedProviders: Set<ComponentName>,
) {

    init {
        requestData.putBoolean(BUNDLE_KEY_IS_AUTO_SELECT_ALLOWED, isAutoSelectAllowed)
        candidateQueryData.putBoolean(BUNDLE_KEY_IS_AUTO_SELECT_ALLOWED, isAutoSelectAllowed)
    }

    /** @hide */
    companion object {
        /** @hide */
        @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
        const val BUNDLE_KEY_IS_AUTO_SELECT_ALLOWED =
            "androidx.credentials.BUNDLE_KEY_IS_AUTO_SELECT_ALLOWED"

        internal fun extractAutoSelectValue(data: Bundle): Boolean {
            return data.getBoolean(BUNDLE_KEY_IS_AUTO_SELECT_ALLOWED)
        }

        /** @hide */
        @JvmStatic
        fun createFrom(
            type: String,
            requestData: Bundle,
            candidateQueryData: Bundle,
            requireSystemProvider: Boolean,
            allowedProviders: Set<ComponentName>,
        ): CredentialOption {
            return try {
                when (type) {
                    PasswordCredential.TYPE_PASSWORD_CREDENTIAL ->
                        GetPasswordOption.createFrom(requestData, allowedProviders)
                    PublicKeyCredential.TYPE_PUBLIC_KEY_CREDENTIAL ->
                        when (requestData.getString(PublicKeyCredential.BUNDLE_KEY_SUBTYPE)) {
                            GetPublicKeyCredentialOption
                                .BUNDLE_VALUE_SUBTYPE_GET_PUBLIC_KEY_CREDENTIAL_OPTION ->
                                GetPublicKeyCredentialOption.createFrom(
                                    requestData, allowedProviders)
                            else -> throw FrameworkClassParsingException()
                        }
                    else -> throw FrameworkClassParsingException()
                }
            } catch (e: FrameworkClassParsingException) {
                // Parsing failed but don't crash the process. Instead just output a request with
                // the raw framework values.
                GetCustomCredentialOption(
                    type = type,
                    requestData = requestData,
                    candidateQueryData = candidateQueryData,
                    isSystemProviderRequired = requireSystemProvider,
                    isAutoSelectAllowed = requestData.getBoolean(
                        BUNDLE_KEY_IS_AUTO_SELECT_ALLOWED, false),
                    allowedProviders = allowedProviders,
                )
            }
        }
    }
}
