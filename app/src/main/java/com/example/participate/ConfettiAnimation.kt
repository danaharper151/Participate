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
            List(50) { // 50 confetti pieces
                ConfettiPiece(
                    color = listOf(
                        Color(0xFF00FF00), // Green
                        Color(0xFF4CAF50), // Light green
                        Color(0xFF8BC34A), // Lime
                        Color(0xFF2196F3), // Blue (water/earth)
                        Color(0xFFFFEB3B), // Yellow (sun)
                        Color(0xFF795548)  // Brown (earth)
                    ).random(),
                    startX = Random.nextFloat(),
                    startY = -0.1f,
                    size = Random.nextFloat() * 15f + 10f,
                    rotation = Random.nextFloat() * 360f,
                    speedY = Random.nextFloat() * 2f + 1f,
                    speedX = (Random.nextFloat() - 0.5f) * 0.5f,
                    shape = Random.nextInt(3) // 0: circle, 1: square, 2: leaf
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
                val rotation = piece.rotation + progress * 720f

                rotate(rotation, Offset(x, y)) {
                    when (piece.shape) {
                        0 -> { // Circle
                            drawCircle(
                                color = piece.color,
                                radius = piece.size / 2,
                                center = Offset(x, y)
                            )
                        }
                        1 -> { // Square
                            drawRect(
                                color = piece.color,
                                topLeft = Offset(x - piece.size / 2, y - piece.size / 2),
                                size = androidx.compose.ui.geometry.Size(piece.size, piece.size)
                            )
                        }
                        2 -> { // Leaf shape
                            val path = Path().apply {
                                moveTo(x, y - piece.size / 2)
                                quadraticBezierTo(
                                    x + piece.size / 2, y,
                                    x, y + piece.size / 2
                                )
                                quadraticBezierTo(
                                    x - piece.size / 2, y,
                                    x, y - piece.size / 2
                                )
                            }
                            drawPath(path, piece.color)
                        }
                    }
                }
            }
        }
    }
}

data class ConfettiPiece(
    val color: Color,
    val startX: Float,
    val startY: Float,
    val size: Float,
    val rotation: Float,
    val speedY: Float,
    val speedX: Float,
    val shape: Int
)