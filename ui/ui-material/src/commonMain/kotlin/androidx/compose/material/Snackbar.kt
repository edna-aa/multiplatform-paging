/*
 * Copyright 2019 The Android Open Source Project
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

package androidx.compose.material

import androidx.compose.foundation.Box
import androidx.compose.foundation.ProvideTextStyle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.relativePaddingFrom
import androidx.compose.foundation.text.FirstBaseline
import androidx.compose.foundation.text.LastBaseline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.AlignmentLine
import androidx.compose.ui.Layout
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.id
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * Snackbars provide brief messages about app processes at the bottom of the screen.
 *
 * Snackbars inform users of a process that an app has performed or will perform. They appear
 * temporarily, towards the bottom of the screen. They shouldn’t interrupt the user experience,
 * and they don’t require user input to disappear.
 *
 * A Snackbar can contain a single action. Because Snackbar disappears automatically, the action
 * shouldn't be "Dismiss" or "Cancel".
 *
 * @sample androidx.compose.material.samples.SimpleSnackbar
 *
 * @param modifier modifiers for the the Snackbar layout
 * @param action action / button component to add as an action to the snackbar. Consider using
 * [SnackbarConstants.defaultActionPrimaryColor] as the color for the action, if you do not
 * have a predefined color you wish to use instead.
 * @param actionOnNewLine whether or not action should be put on the separate line. Recommended
 * for action with long action text
 * @param shape Defines the Snackbar's shape as well as its shadow
 * @param backgroundColor background color of the Snackbar
 * @param contentColor color of the content to use inside the snackbar. Defaults to
 * either the matching `onFoo` color for [backgroundColor], or, if it is not a color from
 * the theme, this will keep the same value set above this Surface.
 * @param elevation The z-coordinate at which to place the SnackBar. This controls the size
 * of the shadow below the SnackBar
 * @param text text component to show information about a process that an app has performed or
 * will perform
 */
@Composable
fun Snackbar(
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
    actionOnNewLine: Boolean = false,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = SnackbarConstants.defaultBackgroundColor,
    contentColor: Color = MaterialTheme.colors.surface,
    elevation: Dp = 6.dp,
    text: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        elevation = elevation,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        ProvideEmphasis(EmphasisAmbient.current.high) {
            val textStyle = MaterialTheme.typography.body2
            ProvideTextStyle(value = textStyle) {
                when {
                    action == null -> TextOnlySnackbar(text)
                    actionOnNewLine -> NewLineButtonSnackbar(text, action)
                    else -> OneRowSnackbar(text, action)
                }
            }
        }
    }
}

/**
 * Object to hold constants used by the [Snackbar]
 */
object SnackbarConstants {

    /**
     * Default alpha of the overlay in the [defaultBackgroundColor]
     */
    private const val SnackbarOverlayAlpha = 0.8f

    /**
     * Default background color of the [Snackbar]
     */
    @Composable
    val defaultBackgroundColor: Color
        get() =
            MaterialTheme.colors.onSurface
                .copy(alpha = SnackbarOverlayAlpha)
                .compositeOver(MaterialTheme.colors.surface)

    /**
     * Provides a best-effort 'primary' color to be used as the primary color inside a [Snackbar].
     * Given that [Snackbar]s have an 'inverted' theme, i.e in a light theme they appear dark, and
     * in a dark theme they appear light, just using [Colors.primary] will not work, and has
     * incorrect contrast.
     *
     * If your light theme has a corresponding dark theme, you should instead directly use
     * [Colors.primary] from the dark theme when in a light theme, and use
     * [Colors.primaryVariant] from the dark theme when in a dark theme.
     *
     * When in a light theme, this function applies a color overlay to [Colors.primary] from
     * [MaterialTheme.colors] to attempt to reduce the contrast, and when in a dark theme this
     * function uses [Colors.primaryVariant].
     */
    @Composable
    val defaultActionPrimaryColor: Color
        get() {
            val colors = MaterialTheme.colors
            return if (colors.isLight) {
                val primary = colors.primary
                val overlayColor = colors.surface.copy(alpha = 0.6f)

                overlayColor.compositeOver(primary)
            } else {
                colors.primaryVariant
            }
        }
}

@Composable
private fun TextOnlySnackbar(text: @Composable () -> Unit) {
    Layout(
        text,
        modifier = Modifier.padding(
            start = HorizontalSpacing,
            end = HorizontalSpacing,
            top = SnackbarVerticalPadding,
            bottom = SnackbarVerticalPadding
        )
    ) { measurables, constraints ->
        require(measurables.size == 1) {
            "text for Snackbar expected to have exactly only one child"
        }
        val textPlaceable = measurables.first().measure(constraints)
        val firstBaseline = textPlaceable[FirstBaseline]
        val lastBaseline = textPlaceable[LastBaseline]
        require(firstBaseline != AlignmentLine.Unspecified) { "No baselines for text" }
        require(lastBaseline != AlignmentLine.Unspecified) { "No baselines for text" }

        val minHeight =
            if (firstBaseline == lastBaseline) {
                SnackbarMinHeightOneLine
            } else {
                SnackbarMinHeightTwoLines
            }
        val containerHeight = max(minHeight.toIntPx(), textPlaceable.height)
        layout(constraints.maxWidth, containerHeight) {
            val textPlaceY = (containerHeight - textPlaceable.height) / 2
            textPlaceable.placeRelative(0, textPlaceY)
        }
    }
}

@Composable
private fun NewLineButtonSnackbar(
    text: @Composable () -> Unit,
    action: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(
                start = HorizontalSpacing,
                end = HorizontalSpacingButtonSide,
                bottom = SeparateButtonExtraY
            )
    ) {
        Box(
            Modifier
                .relativePaddingFrom(LastBaseline, after = LongButtonVerticalOffset)
                .relativePaddingFrom(FirstBaseline, before = HeightToFirstLine)
                .padding(end = HorizontalSpacingButtonSide),
            children = text
        )
        Box(Modifier.gravity(Alignment.End), children = action)
    }
}

@Composable
private fun OneRowSnackbar(
    text: @Composable () -> Unit,
    action: @Composable () -> Unit
) {
    val textTag = "text"
    val actionTag = "action"
    Layout(
        {
            Box(Modifier.layoutId(textTag), children = text)
            Box(Modifier.layoutId(actionTag), children = action)
        },
        modifier = Modifier.padding(
            start = HorizontalSpacing,
            end = HorizontalSpacingButtonSide,
            top = SnackbarVerticalPadding,
            bottom = SnackbarVerticalPadding
        )
    ) { measurables, constraints ->
        val buttonPlaceable = measurables.first { it.id == actionTag }.measure(constraints)
        val textMaxWidth =
            (constraints.maxWidth - buttonPlaceable.width - TextEndExtraSpacing.toIntPx())
                .coerceAtLeast(constraints.minWidth)
        val textPlaceable = measurables.first { it.id == textTag }.measure(
            constraints.copy(minHeight = 0, maxWidth = textMaxWidth)
        )

        val firstTextBaseline = textPlaceable[FirstBaseline]
        require(firstTextBaseline != AlignmentLine.Unspecified) { "No baselines for text" }
        val lastTextBaseline = textPlaceable[LastBaseline]
        require(lastTextBaseline != AlignmentLine.Unspecified) { "No baselines for text" }
        val isOneLine = firstTextBaseline == lastTextBaseline
        val buttonPlaceX = constraints.maxWidth - buttonPlaceable.width

        val textPlaceY: Int
        val containerHeight: Int
        val buttonPlaceY: Int
        if (isOneLine) {
            val minContainerHeight = SnackbarMinHeightOneLine.toIntPx()
            val contentHeight = buttonPlaceable.height
            containerHeight = max(minContainerHeight, contentHeight)
            textPlaceY = (containerHeight - textPlaceable.height) / 2
            val buttonBaseline = buttonPlaceable[FirstBaseline]
            buttonPlaceY = buttonBaseline.let {
                if (it != AlignmentLine.Unspecified) {
                    textPlaceY + firstTextBaseline - it
                } else {
                    0
                }
            }
        } else {
            val baselineOffset = HeightToFirstLine.toIntPx()
            textPlaceY = baselineOffset - firstTextBaseline - SnackbarVerticalPadding.toIntPx()
            val minContainerHeight = SnackbarMinHeightTwoLines.toIntPx()
            val contentHeight = textPlaceY + textPlaceable.height
            containerHeight = max(minContainerHeight, contentHeight)
            buttonPlaceY = (containerHeight - buttonPlaceable.height) / 2
        }

        layout(constraints.maxWidth, containerHeight) {
            textPlaceable.placeRelative(0, textPlaceY)
            buttonPlaceable.placeRelative(buttonPlaceX, buttonPlaceY)
        }
    }
}

private val HeightToFirstLine = 30.dp
private val HorizontalSpacing = 16.dp
private val HorizontalSpacingButtonSide = 8.dp
private val SeparateButtonExtraY = 8.dp
private val SnackbarVerticalPadding = 6.dp
private val TextEndExtraSpacing = 8.dp
private val LongButtonVerticalOffset = 18.dp
private val SnackbarMinHeightOneLine = 48.dp - SnackbarVerticalPadding * 2
private val SnackbarMinHeightTwoLines = 68.dp - SnackbarVerticalPadding * 2