package com.example.reorderablepracticecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import com.example.reorderablepracticecompose.ui.theme.ReorderablePracticeComposeTheme
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import sh.calvin.reorderable.ReorderableItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReorderablePracticeComposeTheme {
                ReorderableScreen()
            }
        }
    }
}

@Composable
fun ReorderableScreen() {
    val view = LocalView.current

    val years = listOf(
        476, 1780, 1776, 1789, 1803,
        1914, 1939, 1947, 1989, 1969
    )

    val correctEvents = listOf(
        "ローマ帝国の滅亡",
        "産業革命",
        "アメリカ独立戦争",
        "フランス革命",
        "ナポレオン戦争",
        "第一次世界大戦",
        "第二次世界大戦",
        "冷戦",
        "ベルリンの壁崩壊",
        "アポロ11号の月面着陸"
    )

    var events by remember { mutableStateOf(correctEvents) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var incorrectAnswers by remember { mutableStateOf(emptyList<Pair<String, String>>()) }

    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        events = events.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
        ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight()
                    .padding(8.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(years) { year ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "$year",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight()
                    .padding(8.dp),
                state = lazyListState,
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(events, key = { it }) { event ->
                    ReorderableItem(reorderableLazyListState, key = event) { isDragging ->
                        val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                        Surface(shadowElevation = elevation) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(event, Modifier.padding(horizontal = 8.dp))
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    modifier = Modifier.draggableHandle(
                                        onDragStarted = {
                                            ViewCompat.performHapticFeedback(
                                                view,
                                                HapticFeedbackConstantsCompat.GESTURE_START
                                            )
                                        },
                                        onDragStopped = {
                                            ViewCompat.performHapticFeedback(
                                                view,
                                                HapticFeedbackConstantsCompat.GESTURE_END
                                            )
                                        },
                                    ),
                                    onClick = {},
                                ) {
                                    Icon(Icons.Rounded.Menu, contentDescription = "Reorder")
                                }
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Button(
                onClick = {
                    val incorrect = mutableListOf<Pair<String, String>>()
                    var correctCount = 0

                    for (i in years.indices) {
                        if (events[i] == correctEvents[i]) {
                            correctCount++
                        } else {
                            incorrect.add(events[i] to correctEvents[i])
                        }
                    }

                    resultMessage = if (correctCount == years.size) {
                        "全問正解！"
                    } else {
                        "${years.size}問中${correctCount}問正解！"
                    }

                    incorrectAnswers = incorrect
                },
                modifier = Modifier.padding(bottom = 48.dp)
            ) {
                Text("回答")
            }

            resultMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )

                incorrectAnswers.forEach { (userAnswer, correctAnswer) ->
                    Text(
                        text = "❌ $userAnswer → 正解: $correctAnswer",
                        color = Color.Red,
                        modifier = Modifier.padding(4.dp)
                    )
                }

                if (incorrectAnswers.isEmpty()) {
                    correctEvents.forEach { event ->
                        Text(
                            text = "⭕️ $event",
                            color = Color.Green,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}
