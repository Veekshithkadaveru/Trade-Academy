package app.krafted.tradeacademy.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import app.krafted.tradeacademy.R
import app.krafted.tradeacademy.data.Asset
import app.krafted.tradeacademy.ui.theme.GainGreen
import app.krafted.tradeacademy.ui.theme.LossRed
import app.krafted.tradeacademy.viewmodel.MarketViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap

private val categories = listOf("All", "Stocks", "Crypto", "Forex", "Commodities")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    marketViewModel: MarketViewModel = viewModel()
) {
    val uiState by marketViewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var sortByChange by remember { mutableStateOf(false) }
    var selectedAsset by remember { mutableStateOf<app.krafted.tradeacademy.data.Asset?>(null) }

    val displayedAssets = uiState.assets
        .filter { asset ->
            val inCategory = uiState.selectedCategory == null || asset.category == uiState.selectedCategory
            val inSearch = searchQuery.isBlank() ||
                asset.name.contains(searchQuery, ignoreCase = true) ||
                asset.id.contains(searchQuery, ignoreCase = true)
            inCategory && inSearch
        }
        .let { list ->
            if (sortByChange) list.sortedByDescending { asset ->
                val current = uiState.livePrices[asset.id] ?: asset.basePrice
                (current - asset.basePrice) / asset.basePrice
            } else list
        }

    val gainers = uiState.assets.count { asset ->
        (uiState.livePrices[asset.id] ?: asset.basePrice) >= asset.basePrice
    }

    Box(modifier = Modifier.fillMaxSize()) {
        VideoBackground(modifier = Modifier.matchParentSize())

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xBB000000),
                            Color(0xCC000000),
                            Color(0xFF000000)
                        )
                    )
                )
        )


        selectedAsset?.let { asset ->
            BuySellSheet(
                asset = asset,
                currentPrice = uiState.livePrices[asset.id] ?: asset.basePrice,
                onDismiss = { selectedAsset = null }
            )
        }

        Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Market",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "${uiState.assets.size} assets · $gainers gaining",
                        fontSize = 12.sp,
                        color = Color(0xFFAAAAAA)
                    )
                }
                LiveIndicator()
            }

            Spacer(modifier = Modifier.height(12.dp))

            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    val isSelected = when (category) {
                        "All" -> uiState.selectedCategory == null
                        else -> uiState.selectedCategory == category
                    }
                    CategoryChip(
                        category = category,
                        isSelected = isSelected,
                        onClick = { marketViewModel.selectCategory(if (category == "All") null else category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (searchQuery.isNotBlank()) "${displayedAssets.size} results" else "${displayedAssets.size} assets",
                    fontSize = 12.sp,
                    color = Color(0xFF888888)
                )
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (sortByChange) Color(0x33FFFFFF) else Color.Transparent)
                        .clickable { sortByChange = !sortByChange }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "↕", fontSize = 12.sp, color = Color.White)
                    Text(
                        text = if (sortByChange) "Top Movers" else "Default",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedAssets, key = { it.id }) { asset ->
                    AssetCard(
                        asset = asset,
                        currentPrice = uiState.livePrices[asset.id] ?: asset.basePrice,
                        onClick = { selectedAsset = asset }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun LiveIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "live")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x22FF4444))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .scale(scale)
                .clip(RoundedCornerShape(50))
                .background(GainGreen)
        )
        Text(text = "LIVE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GainGreen)
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x33FFFFFF))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color(0xFF888888), modifier = Modifier.size(18.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
            cursorBrush = SolidColor(Color.White),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text("Search assets...", fontSize = 14.sp, color = Color(0xFF666666))
                }
                inner()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CategoryChip(category: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF2196F3) else Color(0x33FFFFFF),
        animationSpec = tween(300),
        label = "chipBg"
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = category,
            color = Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
        )
    }
}

@Composable
fun AssetCard(asset: Asset, currentPrice: Double, onClick: () -> Unit) {
    val priceChange = currentPrice - asset.basePrice
    val isUp = priceChange >= 0
    val percentChange = (priceChange / asset.basePrice) * 100

    val priceColor by animateColorAsState(
        targetValue = if (isUp) GainGreen else LossRed,
        animationSpec = tween(300),
        label = "priceColor"
    )

    val categoryColor = categoryColor(asset.category)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "cardScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x26FFFFFF))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(72.dp)
                .align(Alignment.CenterStart)
                .background(
                    Brush.verticalGradient(listOf(categoryColor, categoryColor.copy(alpha = 0.2f)))
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(categoryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = asset.id.take(2),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = categoryColor
                    )
                }
                Column {
                    Text(
                        text = asset.id,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = asset.name,
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniSparkline(isUp = isUp, modifier = Modifier.width(48.dp).height(28.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatPrice(currentPrice),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(priceColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${if (isUp) "+" else ""}${"%.2f".format(percentChange)}%",
                            color = priceColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiniSparkline(isUp: Boolean, modifier: Modifier = Modifier) {
    val color = if (isUp) GainGreen else LossRed
    val bars = listOf(0.4f, 0.6f, 0.5f, 0.7f, 0.55f, 0.8f, if (isUp) 0.95f else 0.3f)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        bars.forEach { height ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(height)
                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    .background(color.copy(alpha = 0.7f))
            )
        }
    }
}

fun formatPrice(price: Double): String {
    return when {
        price >= 10000 -> "${"$"}${"%.0f".format(price)}"
        price >= 1000 -> "${"$"}${"%.2f".format(price)}"
        price >= 1 -> "${"$"}${"%.2f".format(price)}"
        else -> "${"$"}${"%.4f".format(price)}"
    }
}

@Composable
fun VideoBackground(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val resId = R.raw.market_bg_video
            val uri = "android.resource://${context.packageName}/$resId"
            setMediaItem(MediaItem.fromUri(uri))
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
            volume = 0f
            prepare()
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> exoPlayer.pause()
                Lifecycle.Event.ON_START -> exoPlayer.play()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = false
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        },
        modifier = modifier
    )
}
