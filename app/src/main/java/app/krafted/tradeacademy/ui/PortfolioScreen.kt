package app.krafted.tradeacademy.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import app.krafted.tradeacademy.data.HoldingEntity
import app.krafted.tradeacademy.data.TradeEntity
import app.krafted.tradeacademy.data.TradeType
import app.krafted.tradeacademy.data.INITIAL_BALANCE
import app.krafted.tradeacademy.ui.theme.GainGreen
import app.krafted.tradeacademy.ui.theme.LossRed
import app.krafted.tradeacademy.viewmodel.MarketViewModel
import app.krafted.tradeacademy.viewmodel.PortfolioViewModel
import java.text.SimpleDateFormat
import java.util.*

private val tradeDateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

@Composable
fun PortfolioScreen(
    marketViewModel: MarketViewModel,
    portfolioViewModel: PortfolioViewModel = viewModel()
) {
    val cashBalance by portfolioViewModel.cashBalance.collectAsState()
    val holdings by portfolioViewModel.holdings.collectAsState()
    val tradeHistory by portfolioViewModel.tradeHistory.collectAsState()
    val marketState by marketViewModel.uiState.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    val portfolioValue = holdings.sumOf { holding ->
        val currentPrice = marketState.livePrices[holding.assetId] ?: holding.avgBuyPrice
        currentPrice * holding.quantity
    }
    val totalValue = cashBalance + portfolioValue

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Wallet", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This will clear all holdings and reset your balance to \$10,000. This cannot be undone.",
                    color = Color(0xFFAAAAAA)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    portfolioViewModel.reset()
                    showResetDialog = false
                }) {
                    Text("Reset", color = LossRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = Color(0xFFAAAAAA))
                }
            },
            containerColor = Color(0xFF1A1A1A),
            tonalElevation = 0.dp
        )
    }

    AppBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Portfolio",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "${holdings.size} position${if (holdings.size == 1) "" else "s"}",
                            fontSize = 12.sp,
                            color = Color(0xFFAAAAAA)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Summary card
            item {
                PortfolioSummaryCard(
                    cashBalance = cashBalance,
                    portfolioValue = portfolioValue,
                    totalValue = totalValue,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Holdings section
            item {
                Text(
                    text = "Holdings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (holdings.isEmpty()) {
                item {
                    PortfolioEmptyState(
                        message = "No holdings yet",
                        subtitle = "Buy assets from the Market tab",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            } else {
                items(holdings, key = { it.assetId }) { holding ->
                    val currentPrice = marketState.livePrices[holding.assetId] ?: holding.avgBuyPrice
                    val assetName = marketState.assets.find { it.id == holding.assetId }?.name ?: holding.assetId
                    val category = marketState.assets.find { it.id == holding.assetId }?.category ?: "Stocks"
                    HoldingCard(
                        holding = holding,
                        currentPrice = currentPrice,
                        assetName = assetName,
                        category = category,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            // Trade History section
            item {
                Text(
                    text = "Trade History",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (tradeHistory.isEmpty()) {
                item {
                    PortfolioEmptyState(
                        message = "No trades yet",
                        subtitle = "Your trade history will appear here",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            } else {
                items(tradeHistory.take(20), key = { it.id }) { trade ->
                    val assetName = marketState.assets.find { it.id == trade.assetId }?.name ?: trade.assetId
                    TradeHistoryRow(
                        trade = trade,
                        assetName = assetName,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            // Reset button
            item {
                Button(
                    onClick = { showResetDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "RESET WALLET",
                        color = LossRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioSummaryCard(
    cashBalance: Double,
    portfolioValue: Double,
    totalValue: Double,
    modifier: Modifier = Modifier
) {
    val pnl = totalValue - INITIAL_BALANCE
    val isProfit = pnl >= 0
    val pnlColor by animateColorAsState(
        targetValue = if (isProfit) GainGreen else LossRed,
        animationSpec = tween(300),
        label = "summaryPnlColor"
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
                .height(128.dp)
                .align(Alignment.CenterStart)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2196F3), Color(0xFF2196F3).copy(alpha = 0.2f))
                    )
                )
        )
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Cash", fontSize = 12.sp, color = Color(0xFF999999))
                    Text(
                        text = formatPrice(cashBalance),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Invested", fontSize = 12.sp, color = Color(0xFF999999))
                    Text(
                        text = formatPrice(portfolioValue),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0x22FFFFFF))
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Value", fontSize = 12.sp, color = Color(0xFF999999))
                    Text(
                        text = formatPrice(totalValue),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(pnlColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${if (isProfit) "+" else ""}${formatPrice(pnl)}",
                        color = pnlColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HoldingCard(
    holding: HoldingEntity,
    currentPrice: Double,
    assetName: String,
    category: String,
    modifier: Modifier = Modifier
) {
    val pnl = (currentPrice - holding.avgBuyPrice) * holding.quantity
    val pnlPercent = if (holding.avgBuyPrice > 0) {
        ((currentPrice - holding.avgBuyPrice) / holding.avgBuyPrice) * 100
    } else 0.0
    val isUp = pnl >= 0

    val pnlColor by animateColorAsState(
        targetValue = if (isUp) GainGreen else LossRed,
        animationSpec = tween(300),
        label = "holdingPnlColor"
    )

    val categoryColor = categoryColor(category)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x26FFFFFF))
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(80.dp)
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(categoryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = holding.assetId.take(2),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = categoryColor
                    )
                }
                Column {
                    Text(
                        text = holding.assetId,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = "${formatQty(holding.quantity)} @ ${formatPrice(holding.avgBuyPrice)}",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatPrice(currentPrice * holding.quantity),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
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

@Composable
private fun TradeHistoryRow(
    trade: TradeEntity,
    assetName: String,
    modifier: Modifier = Modifier
) {
    val isBuy = trade.type == TradeType.BUY
    val typeColor = if (isBuy) GainGreen else LossRed
    val dateStr = tradeDateFormat.format(Date(trade.timestamp))

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
                        .background(typeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBuy) "B" else "S",
                        color = typeColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
                Column {
                    Text(
                        text = "${if (isBuy) "Bought" else "Sold"} $assetName",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        color = Color(0xFF777777)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatPrice(trade.price * trade.quantity),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = "${formatQty(trade.quantity)} @ ${formatPrice(trade.price)}",
                    fontSize = 11.sp,
                    color = Color(0xFF777777)
                )
            }
        }
    }
}

@Composable
private fun PortfolioEmptyState(
    message: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x1AFFFFFF))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF888888))
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 12.sp, color = Color(0xFF555555))
        }
    }
}

private fun formatQty(qty: Double): String =
    "%.6f".format(qty).trimEnd('0').trimEnd('.')
