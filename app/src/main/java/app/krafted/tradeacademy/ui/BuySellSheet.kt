package app.krafted.tradeacademy.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.tradeacademy.data.Asset
import app.krafted.tradeacademy.ui.theme.GainGreen
import app.krafted.tradeacademy.ui.theme.LossRed
import app.krafted.tradeacademy.viewmodel.BuySellViewModel
import app.krafted.tradeacademy.viewmodel.TradeResult

private fun categoryColor(category: String): Color = when (category) {
    "Stocks" -> Color(0xFF2196F3)
    "Crypto" -> Color(0xFFF7931A)
    "Forex" -> Color(0xFF9C27B0)
    "Commodities" -> Color(0xFFFFD700)
    else -> Color(0xFF607D8B)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuySellSheet(
    asset: Asset,
    currentPrice: Double,
    onDismiss: () -> Unit,
    viewModel: BuySellViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var quantityText by remember { mutableStateOf("") }
    var isBuyMode by remember { mutableStateOf(true) }

    val quantity = quantityText.toDoubleOrNull() ?: 0.0
    val totalValue = quantity * currentPrice
    val canBuy = quantity > 0 && totalValue <= uiState.cashBalance
    val canSell = quantity > 0 && quantity <= uiState.currentHolding

    val accentColor = categoryColor(asset.category)
    val actionColor = if (isBuyMode) GainGreen else LossRed

    LaunchedEffect(asset.id) {
        viewModel.loadAssetContext(asset.id)
    }

    LaunchedEffect(uiState.tradeResult) {
        if (uiState.tradeResult is TradeResult.Success) {
            kotlinx.coroutines.delay(1200)
            viewModel.clearResult()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0A0A14),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF333355))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = asset.id.take(2),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = asset.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = asset.id,
                            fontSize = 13.sp,
                            color = Color(0xFF888888)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(accentColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = asset.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = accentColor
                            )
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatPrice(currentPrice),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    val priceChange = currentPrice - asset.basePrice
                    val isUp = priceChange >= 0
                    val pct = (priceChange / asset.basePrice) * 100
                    val changeColor = if (isUp) GainGreen else LossRed
                    Text(
                        text = "${if (isUp) "+" else ""}${"%.2f".format(pct)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = changeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x1AFFFFFF))
                    .padding(4.dp)
            ) {
                listOf(true to "BUY", false to "SELL").forEach { (isBuy, label) ->
                    val selected = isBuyMode == isBuy
                    val bgColor by animateColorAsState(
                        targetValue = if (selected) (if (isBuy) GainGreen else LossRed) else Color.Transparent,
                        animationSpec = tween(200),
                        label = "toggleBg"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(11.dp))
                            .background(bgColor)
                            .clickable {
                                isBuyMode = isBuy
                                quantityText = ""
                                viewModel.clearResult()
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (selected) Color.White else Color(0xFF666666),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x1AFFFFFF))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Available Cash", fontSize = 13.sp, color = Color(0xFF888888))
                        Text(
                            text = formatPrice(uiState.cashBalance),
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (uiState.currentHolding > 0) {
                        HorizontalDivider(color = Color(0x1AFFFFFF))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Current Holdings", fontSize = 13.sp, color = Color(0xFF888888))
                            Text(
                                text = "${"%.4f".format(uiState.currentHolding)} ${asset.id}",
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Quantity", fontSize = 13.sp, color = Color(0xFF888888))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x1AFFFFFF))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = quantityText,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                                quantityText = input
                            }
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        cursorBrush = SolidColor(actionColor),
                        decorationBox = { inner ->
                            if (quantityText.isEmpty()) {
                                Text("0.00", fontSize = 18.sp, color = Color(0xFF444466))
                            }
                            inner()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = asset.id,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF666666)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val percentages = listOf(25, 50, 75, 100)
                    percentages.forEach { pct ->
                        val label = if (pct == 100) "MAX" else "${pct}%"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x1AFFFFFF))
                                .clickable {
                                    val maxQty = if (isBuyMode) {
                                        if (currentPrice > 0) uiState.cashBalance / currentPrice else 0.0
                                    } else {
                                        uiState.currentHolding
                                    }
                                    val filled = maxQty * pct / 100.0
                                    quantityText = if (filled > 0) "%.4f".format(filled).trimEnd('0').trimEnd('.') else ""
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = actionColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = quantity > 0,
                enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x1AFFFFFF))
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(60.dp)
                            .align(Alignment.CenterStart)
                            .background(
                                Brush.verticalGradient(
                                    listOf(actionColor, actionColor.copy(alpha = 0.2f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier.padding(start = 16.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Order Type", fontSize = 12.sp, color = Color(0xFF888888))
                            Text(
                                text = if (isBuyMode) "Market Buy" else "Market Sell",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = actionColor
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${"%.4f".format(quantity)} x ${formatPrice(currentPrice)}",
                                fontSize = 12.sp,
                                color = Color(0xFF888888)
                            )
                            Text(
                                text = formatPrice(totalValue),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = uiState.tradeResult is TradeResult.Error,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val msg = (uiState.tradeResult as? TradeResult.Error)?.message ?: ""
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(LossRed.copy(alpha = 0.12f))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = msg,
                        color = LossRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            AnimatedVisibility(
                visible = uiState.tradeResult is TradeResult.Success,
                enter = fadeIn(tween(200)) + scaleIn(tween(300)),
                exit = fadeOut(tween(200))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(GainGreen.copy(alpha = 0.12f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Trade Confirmed",
                        color = GainGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val isEnabled = if (isBuyMode) canBuy else canSell
            val buttonAlpha by animateFloatAsState(
                targetValue = if (isEnabled) 1f else 0.35f,
                animationSpec = tween(200),
                label = "btnAlpha"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isEnabled && uiState.tradeResult !is TradeResult.Success)
                            Brush.horizontalGradient(
                                listOf(actionColor, actionColor.copy(alpha = 0.75f))
                            )
                        else
                            Brush.horizontalGradient(
                                listOf(
                                    actionColor.copy(alpha = 0.25f),
                                    actionColor.copy(alpha = 0.15f)
                                )
                            )
                    )
                    .clickable(enabled = isEnabled && !uiState.isLoading && uiState.tradeResult !is TradeResult.Success) {
                        viewModel.clearResult()
                        if (isBuyMode) viewModel.buy(asset.id, currentPrice, quantity)
                        else viewModel.sell(asset.id, currentPrice, quantity)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isBuyMode) "Confirm Buy" else "Confirm Sell",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = buttonAlpha)
                    )
                }
            }
        }
    }
}
