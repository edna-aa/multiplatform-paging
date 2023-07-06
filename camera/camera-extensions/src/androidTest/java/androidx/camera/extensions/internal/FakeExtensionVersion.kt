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

package androidx.camera.extensions.internal

import androidx.test.filters.SdkSuppress

/**
 * A fake extension version with a settable version string used for testing
 */
@SdkSuppress(minSdkVersion = 23) // BasicVendorExtender requires API level 23
class FakeExtensionVersion(
    private val version: Version = Version.VERSION_1_3,
    private val isAdvancedExtenderSupported: Boolean = true
) : ExtensionVersion() {
    override fun isAdvancedExtenderSupportedInternal(): Boolean = isAdvancedExtenderSupported

    override fun getVersionObject(): Version = version
}
