/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.ui.androidview.demos

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.Composition
import androidx.compose.Recomposer
import androidx.compose.state
import androidx.ui.androidview.adapters.setOnClick
import androidx.ui.core.Modifier
import androidx.ui.core.gesture.tapGestureFilter
import androidx.ui.core.setContent
import androidx.ui.demos.common.ActivityDemo
import androidx.ui.demos.common.DemoCategory
import androidx.ui.foundation.Box
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.clickable
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.graphics.RectangleShape
import androidx.ui.graphics.toArgb
import androidx.ui.layout.Column
import androidx.ui.layout.fillMaxHeight
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredSize
import androidx.ui.layout.preferredWidth
import androidx.ui.layout.wrapContentSize
import androidx.ui.unit.dp

val ComposeInAndroidDemos = DemoCategory(
    "Compose in Android Interop", listOf(
        ActivityDemo(
            "Compose with no gestures in Android tap",
            ComposeNothingInAndroidTap::class
        ),
        ActivityDemo(
            "Compose tap in Android tap",
            ComposeTapInAndroidTap::class
        ),
        ActivityDemo(
            "Compose tap in Android scroll",
            ComposeTapInAndroidScroll::class
        ),
        ActivityDemo(
            "Compose scroll in Android scroll (same orientation)",
            ComposeScrollInAndroidScrollSameOrientation::class
        ),
        ActivityDemo(
            "Compose scroll in Android scroll (different orientations)",
            ComposeScrollInAndroidScrollDifferentOrientation::class
        )
    )
)

open class ComposeNothingInAndroidTap : ComponentActivity() {

    private var currentColor = Color.DarkGray

    private lateinit var composition: Composition

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compose_in_android_tap)

        findViewById<TextView>(R.id.text1).text =
            "Intended to Demonstrate that when no gestureFilterModifiers are added to compose, " +
                    "Compose will not interact with the pointer input stream. This currently " +
                    "isn't actually the case however. "

        findViewById<TextView>(R.id.text2).text =
            "When you tap anywhere within the bounds of the colored, including the grey box in " +
                    "the middle, the color is supposed to change.  This currently does not occur " +
                    "when you tap on the grey box however."

        val container = findViewById<ViewGroup>(R.id.clickableContainer)
        container.isClickable = true
        container.setBackgroundColor(currentColor.toArgb())
        container.setOnClick {
            currentColor = if (currentColor == Color.Green) {
                Color.Red
            } else {
                Color.Green
            }
            container.setBackgroundColor(currentColor.toArgb())
        }
        composition = container.setContent(Recomposer.current()) {
            Box(Modifier.drawBackground(Color.LightGray, RectangleShape).fillMaxSize())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        composition.dispose()
    }
}

open class ComposeTapInAndroidTap : ComponentActivity() {

    private var currentColor = Color.DarkGray

    private lateinit var composition: Composition

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compose_in_android_tap)

        findViewById<TextView>(R.id.text1).text =
            "Demonstrates correct interop with simple tapping"
        findViewById<TextView>(R.id.text2).text =
            "The inner box is Compose, the outer is Android.  When you tap on the inner box, " +
                    "only it changes colors. When you tap on the outer box, only the outer box " +
                    "changes colors."

        val container = findViewById<ViewGroup>(R.id.clickableContainer)
        container.isClickable = true
        container.setBackgroundColor(currentColor.toArgb())
        container.setOnClick {
            currentColor = if (currentColor == Color.Green) {
                Color.Red
            } else {
                Color.Green
            }
            container.setBackgroundColor(currentColor.toArgb())
        }

        composition = container.setContent(Recomposer.current()) {

            val currentColor = state { Color.LightGray }

            val tap =
                Modifier.tapGestureFilter {
                    currentColor.value =
                        if (currentColor.value == Color.Blue) Color.Yellow else Color.Blue
                }

            Column {
                Box(
                    tap + Modifier.drawBackground(currentColor.value, RectangleShape).fillMaxSize()
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        composition.dispose()
    }
}

open class ComposeTapInAndroidScroll : ComponentActivity() {

    private lateinit var composition: Composition

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compose_in_android_scroll)

        findViewById<View>(R.id.container).setBackgroundColor(Color.DarkGray.toArgb())

        findViewById<TextView>(R.id.text1).text =
            "Demonstrates that press gestures and movement gestures interact correctly between " +
                    "Android and Compose when Compose is inside of Android."

        findViewById<TextView>(R.id.text2).text =
            "The inner box is Compose, the rest is Android.  Tapping the inner box will change " +
                    "it's color.  Putting a finger down on the inner box and dragging vertically," +
                    " will cause the outer Android ScrollView to scroll and removing the finger " +
                    "from the screen will not cause the Compose box to change colors. "

        val container = findViewById<ViewGroup>(R.id.container)
        composition = container.setContent(Recomposer.current()) {

            val currentColor = state { Color.LightGray }

            Box(
                Modifier
                    .drawBackground(Color.Gray, RectangleShape)
                    .fillMaxWidth()
                    .preferredHeight(456.dp)
                    .wrapContentSize()
                    .clickable {
                        currentColor.value =
                            if (currentColor.value == Color.Blue) Color.Yellow else Color.Blue
                    }
                    .drawBackground(currentColor.value, RectangleShape)
                    .preferredSize(192.dp)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        composition.dispose()
    }
}

open class ComposeScrollInAndroidScrollSameOrientation : ComponentActivity() {

    private lateinit var composition: Composition

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compose_in_android_scroll)

        findViewById<View>(R.id.container).setBackgroundColor(Color.DarkGray.toArgb())

        findViewById<TextView>(R.id.text1).text =
            "Intended to demonstrate that scrolling between 2 scrollable things interops " +
                    "\"correctly\" between Compose and Android when Compose is inside Android. " +
                    "This currently does not actually work because nested scrolling interop is " +
                    "not complete."

        findViewById<TextView>(R.id.text2).text =
            "The outer scrollable container always wins because it always intercepts the scroll " +
                    "before the child scrolling container can start scrolling."

        val container = findViewById<ViewGroup>(R.id.container)
        composition = container.setContent(Recomposer.current()) {
            VerticalScroller(
                modifier = Modifier
                    .padding(48.dp)
                    .drawBackground(Color.Gray, RectangleShape)
                    .fillMaxWidth()
                    .preferredHeight(456.dp)
            ) {
                Box(
                    Modifier
                        .padding(48.dp)
                        .drawBackground(Color.LightGray, RectangleShape)
                        .fillMaxWidth()
                        .preferredHeight(456.dp)
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        composition.dispose()
    }
}

open class ComposeScrollInAndroidScrollDifferentOrientation : ComponentActivity() {

    private lateinit var composition: Composition

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.compose_in_android_scroll)

        findViewById<View>(R.id.container).setBackgroundColor(Color.DarkGray.toArgb())

        findViewById<TextView>(R.id.text1).text =
            "Demonstrates that scrolling in Compose and scrolling in Android interop correctly " +
                    "when Compose is inside of Android."

        findViewById<TextView>(R.id.text2).text =
            "The inner scrollable container is Compose, the other one is Android. You can only " +
                    "scroll in one orientation at a time."

        val container = findViewById<ViewGroup>(R.id.container)
        composition = container.setContent(Recomposer.current()) {
            HorizontalScroller(
                modifier = Modifier
                    .padding(48.dp)
                    .drawBackground(Color.Gray, RectangleShape)
                    .fillMaxWidth()
                    .preferredHeight(456.dp)
            ) {
                Box(
                    Modifier
                        .padding(48.dp)
                        .drawBackground(Color.LightGray, RectangleShape)
                        .preferredWidth(360.dp)
                        .fillMaxHeight()
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        composition.dispose()
    }
}