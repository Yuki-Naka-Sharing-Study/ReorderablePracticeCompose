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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.reorderablepracticecompose.ui.theme.ReorderablePracticeComposeTheme
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
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

    // 年号リスト（正解）
    val years = listOf(
        476,  // ローマ帝国の滅亡
        1780, // 産業革命
        1776, // アメリカ独立戦争
        1789, // フランス革命
        1803, // ナポレオン戦争
        1914, // 第一次世界大戦
        1939, // 第二次世界大戦
        1947, // 冷戦
        1969,  // アポロ11号の月面着陸
        1989, // ベルリンの壁崩壊
    )

    // **初期化時にシャッフル**
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
            "アポロ11号の月面着陸",
            "ベルリンの壁崩壊",
        ).shuffled()  // 🔥ここでランダム化🔥
    )}

    // ダイアログの状態を管理
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    // 並べ替え用の状態
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        events = events.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp)
    ) {
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
                            .height(50.dp),
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
                                    .height(50.dp)
                            ) {
                                Text(event, Modifier.padding(horizontal = 8.dp))
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    modifier = Modifier.draggableHandle(),
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
            onClick = {
                // 回答チェック
                val correctEvents = listOf(
                    "ローマ帝国の滅亡",
                    "産業革命",
                    "アメリカ独立戦争",
                    "フランス革命",
                    "ナポレオン戦争",
                    "第一次世界大戦",
                    "第二次世界大戦",
                    "冷戦",
                    "アポロ11号の月面着陸",
                    "ベルリンの壁崩壊",
                )

                val results = events.zip(correctEvents) { userAnswer, correctAnswer ->
                    if (userAnswer == correctAnswer) "⭕ $userAnswer" else "❌ $userAnswer (正解: $correctAnswer)"
                }

                val correctCount = results.count { it.startsWith("⭕") }

                dialogMessage = if (correctCount == years.size) {
                    "全問正解！ 🎉"
                } else {
                    "${years.size}問中${correctCount}問正解！\n\n" + results.joinToString("\n")
                }

                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            Text("回答")
        }
    }

    // 結果を表示するダイアログ
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("閉じる")
                }
            },
            title = { Text("結果発表") },
            text = { Text(dialogMessage) }
        )
    }
}
