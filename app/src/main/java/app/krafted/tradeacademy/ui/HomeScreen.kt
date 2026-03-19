package app.krafted.tradeacademy.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.navigation.NavController
import app.krafted.tradeacademy.data.INITIAL_BALANCE
import app.krafted.tradeacademy.ui.theme.GainGreen
import app.krafted.tradeacademy.ui.theme.LossRed
import app.krafted.tradeacademy.viewmodel.MarketViewModel
import app.krafted.tradeacademy.viewmodel.PortfolioViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    marketViewModel: MarketViewModel,
    portfolioViewModel: PortfolioViewModel = viewModel()
) {
    val cashBalance by portfolioViewModel.cashBalance.collectAsState()
    val holdings by portfolioViewModel.holdings.collectAsState()
    val marketState by marketViewModel.uiState.collectAsState()

    val portfolioValue = holdings.sumOf { holding ->
        val currentPrice = marketState.livePrices[holding.assetId] ?: holding.avgBuyPrice
        currentPrice * holding.quantity
    }
    val totalValue = cashBalance + portfolioValue
    val pnl = totalValue - INITIAL_BALANCE
    val isProfit = pnl >= 0


    val topMovers = marketState.assets
        .map { asset ->
            val current = marketState.livePrices[asset.id] ?: asset.basePrice
            val change = ((current - asset.basePrice) / asset.basePrice) * 100
            Triple(asset, current, change)
        }
        .sortedByDescending { kotlin.math.abs(it.third) }
        .take(5)

    AppBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Trade Academy",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Your trading dashboard",
                        fontSize = 12.sp,
                        color = Color(0xFFAAAAAA)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }


            item {
                HomeBalanceCard(
                    cashBalance = cashBalance,
                    portfolioValue = portfolioValue,
                    totalValue = totalValue,
                    pnl = pnl,
                    isProfit = isProfit,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }


            item {
                Text(
                    text = "Quick Access",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickNavButton(
                        label = "Market",
                        subtitle = "${marketState.assets.size} assets",
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("market") }
                    )
                    QuickNavButton(
                        label = "News",
                        subtitle = "Latest updates",
                        color = Color(0xFF9C27B0),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("news_tips") }
                    )
                    QuickNavButton(
                        label = "Portfolio",
                        subtitle = "${holdings.size} held",
                        color = Color(0xFFF7931A),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("portfolio") }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }


            if (topMovers.isNotEmpty()) {
                item {
                    Text(
                        text = "Top Movers",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(topMovers, key = { it.first.id }) { (asset, price, change) ->
                            TopMoverChip(
                                assetId = asset.id,
                                price = price,
                                change = change,
                                category = asset.category
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }


            if (holdings.isNotEmpty()) {
                item {
                    Text(
                        text = "Your Holdings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(holdings, key = { it.assetId }) { holding ->
                    val currentPrice = marketState.livePrices[holding.assetId] ?: holding.avgBuyPrice
                    val holdingPnl = (currentPrice - holding.avgBuyPrice) * holding.quantity
                    val holdingPnlPercent = if (holding.avgBuyPrice > 0) {
                        ((currentPrice - holding.avgBuyPrice) / holding.avgBuyPrice) * 100
                    } else 0.0
                    val category = marketState.assets.find { it.id == holding.assetId }?.category ?: "Stocks"

                    HomeHoldingRow(
                        assetId = holding.assetId,
                        value = currentPrice * holding.quantity,
                        pnlPercent = holdingPnlPercent,
                        category = category,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeBalanceCard(
    cashBalance: Double,
    portfolioValue: Double,
    totalValue: Double,
    pnl: Double,
    isProfit: Boolean,
    modifier: Modifier = Modifier
) {
    val pnlColor by animateColorAsState(
        targetValue = if (isProfit) GainGreen else LossRed,
        animationSpec = tween(300),
        label = "homePnlColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x26FFFFFF))
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(160.dp)
                .align(Alignment.CenterStart)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2196F3), Color(0xFF2196F3).copy(alpha = 0.2f))
                    )
                )
        )
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 16.dp, top = 20.dp, bottom = 20.dp)
        ) {
            Text("Total Value", fontSize = 12.sp, color = Color(0xFF999999))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatPrice(totalValue),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(pnlColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${if (isProfit) "+" else ""}${formatPrice(pnl)} (${if (isProfit) "+" else ""}${"%.2f".format((pnl / INITIAL_BALANCE) * 100)}%)",
                    color = pnlColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Cash", fontSize = 12.sp, color = Color(0xFF999999))
                    Text(
                        text = formatPrice(cashBalance),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Invested", fontSize = 12.sp, color = Color(0xFF999999))
                    Text(
                        text = formatPrice(portfolioValue),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickNavButton(
    label: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x26FFFFFF))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(72.dp)
                .align(Alignment.CenterStart)
                .background(
                    Brush.verticalGradient(listOf(color, color.copy(alpha = 0.2f)))
                )
        )
        Column(
            modifier = Modifier.padding(start = 14.dp, end = 12.dp, top = 14.dp, bottom = 14.dp)
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF777777)
            )
        }
    }
}

@Composable
private fun TopMoverChip(
    assetId: String,
    price: Double,
    change: Double,
    category: String
) {
    val isUp = change >= 0
    val changeColor = if (isUp) GainGreen else LossRed
    val catColor = categoryColor(category)

    Box(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x26FFFFFF))
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(72.dp)
                .align(Alignment.CenterStart)
                .background(
                    Brush.verticalGradient(listOf(catColor, catColor.copy(alpha = 0.2f)))
                )
        )
        Column(
            modifier = Modifier.padding(start = 14.dp, end = 12.dp, top = 12.dp, bottom = 12.dp)
        ) {
            Text(
                text = assetId,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatPrice(price),
                fontSize = 12.sp,
                color = Color(0xFF999999)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(changeColor.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${if (isUp) "+" else ""}${"%.2f".format(change)}%",
                    color = changeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun HomeHoldingRow(
    assetId: String,
    value: Double,
    pnlPercent: Double,
    category: String,
    modifier: Modifier = Modifier
) {
    val isUp = pnlPercent >= 0
    val pnlColor by animateColorAsState(
        targetValue = if (isUp) GainGreen else LossRed,
        animationSpec = tween(300),
        label = "homeHoldingPnl"
    )
    val catColor = categoryColor(category)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x1AFFFFFF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(catColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = assetId.take(2),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = catColor
                    )
                }
                Text(
                    text = assetId,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = formatPrice(value),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(pnlColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${if (isUp) "+" else ""}${"%.2f".format(pnlPercent)}%",
                        color = pnlColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
