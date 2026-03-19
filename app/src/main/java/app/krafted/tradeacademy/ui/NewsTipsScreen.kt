package app.krafted.tradeacademy.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.tradeacademy.data.Article
import app.krafted.tradeacademy.data.Tip
import app.krafted.tradeacademy.viewmodel.ContentViewModel

private val tabs = listOf("News", "Tips")

private fun difficultyColor(difficulty: String): Color = when (difficulty) {
    "Beginner" -> Color(0xFF4CAF50)
    "Intermediate" -> Color(0xFFF7931A)
    "Advanced" -> Color(0xFFF44336)
    else -> Color(0xFF607D8B)
}

private fun tipIconEmoji(icon: String): String = when (icon) {
    "trending_up" -> "\u2197\uFE0F"
    "pie_chart" -> "\uD83D\uDCC8"
    "warning" -> "\u26A0\uFE0F"
    "timeline" -> "\uD83D\uDCC9"
    "psychology" -> "\uD83E\uDDE0"
    "account_balance" -> "\uD83C\uDFE6"
    "show_chart" -> "\uD83D\uDCC8"
    "article" -> "\uD83D\uDCF0"
    "history" -> "\uD83D\uDCDD"
    "lightbulb" -> "\uD83D\uDCA1"
    else -> "\uD83D\uDCCC"
}

@Composable
fun NewsTipsScreen(contentViewModel: ContentViewModel = viewModel()) {
    val uiState by contentViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var expandedNewsId by remember { mutableStateOf<Int?>(null) }
    var expandedTipId by remember { mutableStateOf<Int?>(null) }

    AppBackground {
        Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "News & Tips",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "${uiState.articles.size} articles \u00B7 ${uiState.tips.size} tips",
                    fontSize = 12.sp,
                    color = Color(0xFFAAAAAA)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))


            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tabs.size) { index ->
                    val isSelected = selectedTab == index
                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF2196F3) else Color(0x33FFFFFF),
                        animationSpec = tween(300),
                        label = "tabBg"
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(bgColor)
                            .clickable { selectedTab = index }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tabs[index],
                            color = Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = if (selectedTab == 0) "${uiState.articles.size} articles" else "${uiState.tips.size} tips",
                fontSize = 12.sp,
                color = Color(0xFF888888),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )


            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.articles, key = { it.id }) { article ->
                            NewsCard(
                                article = article,
                                isExpanded = expandedNewsId == article.id,
                                onClick = {
                                    expandedNewsId = if (expandedNewsId == article.id) null else article.id
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
                1 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.tips, key = { it.id }) { tip ->
                            TipCard(
                                tip = tip,
                                isExpanded = expandedTipId == tip.id,
                                onClick = {
                                    expandedTipId = if (expandedTipId == tip.id) null else tip.id
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsCard(article: Article, isExpanded: Boolean, onClick: () -> Unit) {
    val accentColor = categoryColor(article.category)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x26FFFFFF))
            .clickable(onClick = onClick)
    ) {

        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .defaultMinSize(minHeight = 72.dp)
                .align(Alignment.CenterStart)
                .background(
                    Brush.verticalGradient(listOf(accentColor, accentColor.copy(alpha = 0.2f)))
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = article.tag,
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = article.date,
                    fontSize = 11.sp,
                    color = Color(0xFF777777)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = article.headline,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))


            Text(
                text = article.summary,
                color = Color(0xFF999999),
                fontSize = 13.sp
            )


            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = article.category,
                color = Color(0xFF777777),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )


            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0x33FFFFFF))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = article.body,
                        color = Color(0xFFCCCCCC),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TipCard(tip: Tip, isExpanded: Boolean, onClick: () -> Unit) {
    val diffColor = difficultyColor(tip.difficulty)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x26FFFFFF))
            .clickable(onClick = onClick)
    ) {
        // Left accent strip
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .defaultMinSize(minHeight = 72.dp)
                .align(Alignment.CenterStart)
                .background(
                    Brush.verticalGradient(listOf(diffColor, diffColor.copy(alpha = 0.2f)))
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(diffColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tipIconEmoji(tip.icon),
                    fontSize = 20.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tip.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(diffColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tip.difficulty,
                            color = diffColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))


                Text(
                    text = tip.body,
                    color = Color(0xFF999999),
                    fontSize = 13.sp,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2
                )


                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(animationSpec = tween(300)),
                    exit = shrinkVertically(animationSpec = tween(300))
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0x33FFFFFF))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Tap to collapse",
                            color = Color(0xFF777777),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
