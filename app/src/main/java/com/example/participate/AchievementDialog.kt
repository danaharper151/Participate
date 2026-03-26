package com.example.participate

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun AchievementUnlockedDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(2.dp, Color(0xFF00FF00), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Achievement Unlocked!!",
                    fontSize = 20.sp,
                    color = Color(0xFF00FF00),
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // app images
                Image(
                    painter = painterResource(
                        id = getDrawableIdFromName(achievement.icon)
                    ),
                    contentDescription = achievement.name,
                    modifier = Modifier.size(180.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = achievement.name,
                    fontSize = 24.sp,
                    color = Color(0xFF00FF00),
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = achievement.description,
                    fontSize = 16.sp,
                    color = Color(0xFF00FF00),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00FF00),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Awesome!!", fontFamily = FontFamily.Serif)
                }
            }
        }
    }
}

// Helper function to convert icon name to drawable resource ID
fun getDrawableIdFromName(iconName: String): Int {
    return when (iconName) {
        "sprout_award" -> R.drawable.sprout_award
        "seedling_award" -> R.drawable.seedling_award
        "youngtree_award" -> R.drawable.youngtree_award
        "forestguardian_award" -> R.drawable.forestguardian_award
        "forestgiant_award" -> R.drawable.forestgiant_award
        "ancientguardian_award" -> R.drawable.ancientguardian_award
        "sunrise_award" -> R.drawable.goodmorningsunrise_award
        else -> R.drawable.sprout_award // Default fallback
    }
}