package com.example.reorderablepracticecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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

    // 年号リスト（歴史的事象に対応した年号を設定）
    val years = listOf(
        476,  // ローマ帝国の滅亡
        1780, // 産業革命
        1776, // アメリカ独立戦争
        1789, // フランス革命
        1803, // ナポレオン戦争
        1914, // 第一次世界大戦
        1939, // 第二次世界大戦
        1947, // 冷戦
        1989, // ベルリンの壁崩壊
        1969  // アポロ11号の月面着陸
    )

    // 歴史的事象リスト
    var events by remember { mutableStateOf(
        listOf(
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
    )}

    // 並べ替え用の状態（歴史的事象）
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        events = events.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }

        ViewCompat.performHapticFeedback(
            view,
            HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK
        )
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(top = 48.dp)
    ) {
        // 年号リスト（固定）
        Row(modifier = Modifier.fillMaxSize()) {
            // 年号リスト（固定）
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
                            .height(50.dp), // 高さを指定して一致させる
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "$year",
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            // 歴史的事象リスト（ドラッグ＆ドロップ対応）
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
                                    .height(50.dp) // 高さを指定して一致させる
                            ) {
                                // 歴史的事象を表示
                                Text(event, Modifier.padding(horizontal = 8.dp))

                                // アイコンを右端に配置
                                Spacer(modifier = Modifier.weight(1f))  // これでアイコンを右端に配置

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

        // 「回答Button」を画面の下中央に配置
        Button(
            onClick = { /* 回答ボタンがクリックされた時の処理 */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp) // 下に少し余白を追加
        ) {
            Text("回答")
        }
    }
}
