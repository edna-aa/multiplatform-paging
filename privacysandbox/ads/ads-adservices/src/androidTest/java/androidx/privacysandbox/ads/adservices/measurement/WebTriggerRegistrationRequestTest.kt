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

package androidx.privacysandbox.ads.adservices.measurement

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class WebTriggerRegistrationRequestTest {
    @Test
    @SdkSuppress(minSdkVersion = 33)
    fun testToString() {
        val result = "WebTriggerRegistrationRequest { WebTriggerParams=[WebTriggerParams " +
            "{ RegistrationUri=www.abc.com, DebugKeyAllowed=false }], Destination=www.abc.com"

        val uri = Uri.parse("www.abc.com")
        val params = listOf(WebTriggerParams(uri, false))
        val request = WebTriggerRegistrationRequest(params, uri)
        Truth.assertThat(request.toString()).isEqualTo(result)
    }

    @Test
    @SdkSuppress(minSdkVersion = 33)
    fun testEquals() {
        val uri = Uri.parse("www.abc.com")

        val params = listOf(WebTriggerParams(uri, false))
        val request1 = WebTriggerRegistrationRequest(params, uri)
        val request2 = WebTriggerRegistrationRequest(params, uri)
        val request3 = WebTriggerRegistrationRequest(
            params,
            Uri.parse("https://abc.com"))

        Truth.assertThat(request1 == request2).isTrue()
        Truth.assertThat(request1 != request3).isTrue()
    }
}