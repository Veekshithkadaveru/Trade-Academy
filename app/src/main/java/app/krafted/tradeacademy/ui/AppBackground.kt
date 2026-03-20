package app.krafted.tradeacademy.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap

private fun buildDotGrid(size: Size): List<Offset> {
    val spacing = 48f
    val points = mutableListOf<Offset>()
    var x = spacing
    while (x < size.width) {
        var y = spacing
        while (y < size.height) {
            points.add(Offset(x, y))
            y += spacing
        }
        x += spacing
    }
    return points
}

@Composable
fun AppBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090C14))
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {

            val topGlow = ShaderBrush(
                RadialGradientShader(
                    center = Offset(0f, 0f),
                    radius = size.width * 0.75f,
                    colors = listOf(Color(0x331A6EFF), Color.Transparent),
                    colorStops = listOf(0f, 1f)
                )
            )
            drawRect(brush = topGlow)


            val bottomGlow = ShaderBrush(
                RadialGradientShader(
                    center = Offset(size.width, size.height),
                    radius = size.width * 0.70f,
                    colors = listOf(Color(0x2A7B2FFF), Color.Transparent),
                    colorStops = listOf(0f, 1f)
                )
            )
            drawRect(brush = bottomGlow)


            val dotColor = Color(0x14FFFFFF)
            val points = buildDotGrid(size)
            drawPoints(
                points = points,
                pointMode = PointMode.Points,
                color = dotColor,
                strokeWidth = 2.4f,
                cap = StrokeCap.Round
            )
        }

        content()
    }
}
