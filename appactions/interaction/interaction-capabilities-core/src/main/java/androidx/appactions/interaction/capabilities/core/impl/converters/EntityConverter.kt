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
package androidx.appactions.interaction.capabilities.core.impl.converters

import androidx.appactions.interaction.proto.Entity

/**
 * Converter from any Type to the Entity proto. This converter is usually used in the direction from
 * app to Assistant. Examples where the converter is needed is the developer setting possible values
 * in Properties or returning "disambiguation entities" from an inventory listener.
 *
 * @param T The T instance is usually a value object provided by the app, e.g. a Timer object from
 *   the built-in-types library. </T>
 */
fun interface EntityConverter<T> {

    /** Converter to an Entity proto. */
    fun convert(type: T): Entity

    companion object {
        fun <T> of(typeSpec: TypeSpec<T>): EntityConverter<T> {
            return EntityConverter { entity ->
                val builder = Entity.newBuilder().setStructValue(typeSpec.toStruct(entity))
                typeSpec.getIdentifier(entity)?.let { builder.setIdentifier(it) }
                builder.build()
            }
        }
    }
}
