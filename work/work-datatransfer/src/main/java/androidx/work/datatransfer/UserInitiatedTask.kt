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

package androidx.work.datatransfer

import android.app.job.JobParameters
import android.content.Context
import java.util.concurrent.CancellationException

/**
 * A class that can perform work asynchronously in UserInitiatedTaskManager.
 */
public abstract class UserInitiatedTask(
    /**
     * A unique identifier for the task.
     */
    val name: String,
    /**
     * The application context.
     */
    val appContext: Context
) {

    /**
     * Override this method to start your actual data transfer work.
     * This method is called on the main thread by default.
     *
     * If the task is cancelled, the app will get a [CancellationException], with one of the
     * following messages:
     * - [JobParameters.STOP_REASON_CONSTRAINT_CONNECTIVITY]
     * - [JobParameters.STOP_REASON_DEVICE_STATE]
     * - [JobParameters.STOP_REASON_TIMEOUT]
     * - [JobParameters.STOP_REASON_USER]
     */
    abstract suspend fun performTask()
}