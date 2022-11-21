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

package androidx.privacysandbox.ads.adservices.java.appsetid

import android.content.Context
import android.os.OutcomeReceiver
import androidx.annotation.RequiresApi
import androidx.privacysandbox.ads.adservices.appsetid.AppSetId
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.google.common.util.concurrent.ListenableFuture
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.invocation.InvocationOnMock

@SmallTest
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 34) // b/259092025
class AppSetIdManagerFuturesTest {
    private lateinit var mContext: Context

    @Before
    fun setUp() {
        mContext = spy(ApplicationProvider.getApplicationContext<Context>())
    }

    @Test
    @SdkSuppress(maxSdkVersion = 33)
    fun testAppSetIdOlderVersions() {
        assertThat(AppSetIdManagerFutures.from(mContext)).isEqualTo(null)
    }

    @Test
    @SdkSuppress(minSdkVersion = 34)
    fun testAppSetIdAsync() {
        val appSetIdManager = mockAppSetIdManager(mContext)
        setupResponse(appSetIdManager)
        val managerCompat = AppSetIdManagerFutures.from(mContext)

        // Actually invoke the compat code.
        val result: ListenableFuture<AppSetId> = managerCompat!!.getAppSetIdAsync()

        // Verify that the result of the compat call is correct.
        verifyResponse(result.get())

        // Verify that the compat code was invoked correctly.
        verify(appSetIdManager).getAppSetId(any(), any())
    }

    @RequiresApi(34)
    private fun mockAppSetIdManager(
        spyContext: Context
    ): android.adservices.appsetid.AppSetIdManager {
        val appSetIdManager = mock(android.adservices.appsetid.AppSetIdManager::class.java)
        `when`(spyContext.getSystemService(android.adservices.appsetid.AppSetIdManager::class.java))
            .thenReturn(appSetIdManager)
        return appSetIdManager
    }

    @RequiresApi(34)
    private fun setupResponse(appSetIdManager: android.adservices.appsetid.AppSetIdManager) {
        // Set up the response that AdIdManager will return when the compat code calls it.
        val appSetId = android.adservices.appsetid.AppSetId(
            "1234",
            android.adservices.appsetid.AppSetId.SCOPE_APP)
        val answer = { args: InvocationOnMock ->
            val receiver = args.getArgument<
                OutcomeReceiver<android.adservices.appsetid.AppSetId, Exception>>(1)
            receiver.onResult(appSetId)
            null
        }
        doAnswer(answer)
            .`when`(appSetIdManager).getAppSetId(
                any(),
                any()
            )
    }

    @RequiresApi(34)
    private fun verifyResponse(appSetId: AppSetId) {
        Assert.assertEquals("1234", appSetId.id)
        Assert.assertEquals(AppSetId.SCOPE_APP, appSetId.scope)
    }
}