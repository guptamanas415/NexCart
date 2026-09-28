package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NexCartBlue
import com.example.ui.theme.NexCartPurple

@Composable
fun NexCartLogo(
    modifier: Modifier = Modifier,
    iconSize: Dp = 34.dp,
    showTagline: Boolean = false,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Monogram Badge
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(iconSize * 0.28f))
                .background(
                    Brush.linearGradient(
                        colors = listOf(NexCartBlue, NexCartPurple)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "N",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = (iconSize.value * 0.58f).sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Nex",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (iconSize.value * 0.55f).sp,
                    color = textColor
                )
                Text(
                    text = "Cart",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (iconSize.value * 0.55f).sp,
                    color = NexCartBlue
                )
            }
            if (showTagline) {
                Text(
                    text = "Shop smarter. Live better.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}
