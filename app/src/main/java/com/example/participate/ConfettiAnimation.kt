package com.example.participate

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ConfettiAnimation(
    modifier: Modifier = Modifier,
    onAnimationEnd: () -> Unit = {}
) {
    var isAnimating by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000) // Animation duration
        isAnimating = false
        onAnimationEnd()
    }

    if (isAnimating) {
        val confettiPieces = remember {
            List(60) { // 60 confetti pieces for a spectacular show!
                ConfettiPiece(
                    color = rainbowColors.random(), // Bright rainbow colors!
                    startX = Random.nextFloat(),
                    startY = -0.1f,
                    size = Random.nextFloat() * 20f + 15f, // Varied sizes: 15-35
                    rotation = Random.nextFloat() * 360f,
                    speedY = Random.nextFloat() * 2f + 1.5f,
                    speedX = (Random.nextFloat() - 0.5f) * 0.6f,
                    shape = Random.nextInt(6), // 0-5: leaf, sparkle, planet, sun, butterfly, leaf2
                    twinkleOffset = Random.nextFloat() * 2f * PI.toFloat()
                )
            }
        }

        val infiniteTransition = rememberInfiniteTransition(label = "confetti")
        val progress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "progress"
        )

        Canvas(modifier = modifier.fillMaxSize()) {
            confettiPieces.forEach { piece ->
                val x = size.width * (piece.startX + piece.speedX * progress)
                val y = size.height * (piece.startY + piece.speedY * progress)
                val rotation = piece.rotation + progress * 720f // Two full rotations

                // Twinkle effect: scale oscillates between 0.6 and 1.0
                val twinkle = 0.8f + 0.2f * sin(progress * 6f * PI.toFloat() + piece.twinkleOffset)

                rotate(rotation, Offset(x, y)) {
                    scale(twinkle, Offset(x, y)) {
                        when (piece.shape) {
                            0 -> drawLeaf(x, y, piece.size, piece.color)
                            1 -> drawSparkle(x, y, piece.size, piece.color)
                            2 -> drawPlanet(x, y, piece.size, piece.color)
                            3 -> drawSunRays(x, y, piece.size, piece.color)
                            4 -> drawButterfly(x, y, piece.size, piece.color)
                            5 -> drawOakLeaf(x, y, piece.size, piece.color)
                        }
                    }
                }
            }
        }
    }
}

// Rainbow colors - bright and vibrant!
val rainbowColors = listOf(
    Color(0xFFFF0000), // Bright Red
    Color(0xFFFF7F00), // Bright Orange
    Color(0xFFFFFF00), // Bright Yellow
    Color(0xFF00FF00), // Bright Green
    Color(0xFF00FFFF), // Bright Cyan
    Color(0xFF0000FF), // Bright Blue
    Color(0xFF8B00FF), // Bright Purple
    Color(0xFFFF00FF), // Bright Magenta
    Color(0xFFFF1493), // Deep Pink
    Color(0xFF00FF7F), // Spring Green
    Color(0xFFFFD700), // Gold
    Color(0xFF7FFF00)  // Chartreuse
)

// Shape drawing functions
fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLeaf(
    x: Float, y: Float, size: Float, color: Color
) {
    val path = Path().apply {
        moveTo(x, y - size / 2)
        quadraticBezierTo(
            x + size / 2, y,
            x, y + size / 2
        )
        quadraticBezierTo(
            x - size / 2, y,
            x, y - size / 2
        )
    }
    drawPath(path, color)
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawOakLeaf(
    x: Float, y: Float, size: Float, color: Color
) {
    val path = Path().apply {
        moveTo(x, y - size / 2)
        // Create wavy edges for oak leaf
        quadraticBezierTo(x + size / 4, y - size / 4, x + size / 3, y)
        quadraticBezierTo(x + size / 4, y + size / 4, x, y + size / 2)
        quadraticBezierTo(x - size / 4, y + size / 4, x - size / 3, y)
        quadraticBezierTo(x - size / 4, y - size / 4, x, y - size / 2)
    }
    drawPath(path, color)
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSparkle(
    x: Float, y: Float, size: Float, color: Color
) {
    // Four-pointed star/sparkle
    val path = Path().apply {
        moveTo(x, y - size / 2) // Top point
        lineTo(x - size / 8, y - size / 8) // Inner point
        lineTo(x - size / 2, y) // Left point
        lineTo(x - size / 8, y + size / 8) // Inner point
        lineTo(x, y + size / 2) // Bottom point
        lineTo(x + size / 8, y + size / 8) // Inner point
        lineTo(x + size / 2, y) // Right point
        lineTo(x + size / 8, y - size / 8) // Inner point
        close()
    }
    drawPath(path, color)
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPlanet(
    x: Float, y: Float, size: Float, color: Color
) {
    // Planet with ring
    drawCircle(
        color = color,
        radius = size / 2,
        center = Offset(x, y)
    )
    // Ring
    val path = Path().apply {
        addOval(
            androidx.compose.ui.geometry.Rect(
                left = x - size / 1.5f,
                top = y - size / 4,
                right = x + size / 1.5f,
                bottom = y + size / 4
            )
        )
    }
    drawPath(
        path = path,
        color = color.copy(alpha = 0.6f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
    )
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSunRays(
    x: Float, y: Float, size: Float, color: Color
) {
    // Center circle
    drawCircle(
        color = color,
        radius = size / 4,
        center = Offset(x, y)
    )
    // 8 rays
    for (i in 0..7) {
        val angle = i * PI.toFloat() / 4
        val startX = x + cos(angle) * size / 3
        val startY = y + sin(angle) * size / 3
        val endX = x + cos(angle) * size / 2
        val endY = y + sin(angle) * size / 2

        drawLine(
            color = color,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 3f
        )
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawButterfly(
    x: Float, y: Float, size: Float, color: Color
) {
    // Simple butterfly with two wings
    val path = Path().apply {
        // Left wing
        moveTo(x, y)
        quadraticBezierTo(
            x - size / 2, y - size / 3,
            x - size / 4, y
        )
        quadraticBezierTo(
            x - size / 2, y + size / 3,
            x, y
        )

        // Right wing
        moveTo(x, y)
        quadraticBezierTo(
            x + size / 2, y - size / 3,
            x + size / 4, y
        )
        quadraticBezierTo(
            x + size / 2, y + size / 3,
            x, y
        )
    }
    drawPath(path, color)

    // Body
    drawLine(
        color = color.copy(alpha = 0.8f),
        start = Offset(x, y - size / 4),
        end = Offset(x, y + size / 4),
        strokeWidth = 3f
    )
}

data class ConfettiPiece(
    val color: Color,
    val startX: Float,
    val startY: Float,
    val size: Float,
    val rotation: Float,
    val speedY: Float,
    val speedX: Float,
    val shape: Int,
    val twinkleOffset: Float
)