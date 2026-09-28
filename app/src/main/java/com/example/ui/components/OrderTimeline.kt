package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen

@Composable
fun OrderTimeline(
    currentStatus: String,
    modifier: Modifier = Modifier
) {
    val status = OrderStatus.fromString(currentStatus)
    val isCancelled = status == OrderStatus.CANCELLED

    val steps = listOf(
        OrderStatus.PENDING to "Order Placed",
        OrderStatus.CONFIRMED to "Confirmed",
        OrderStatus.PACKED to "Packed",
        OrderStatus.SHIPPED to "Shipped",
        OrderStatus.DELIVERED to "Delivered"
    )

    val currentStepIndex = if (isCancelled) -1 else steps.indexOfFirst { it.first == status }.coerceAtLeast(0)

    Column(modifier = modifier.fillMaxWidth()) {
        if (isCancelled) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ErrorRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancelled",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Order Cancelled",
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "This order has been cancelled and refunded if pre-paid.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            steps.forEachIndexed { index, pair ->
                val isCompleted = index <= currentStepIndex
                val isCurrent = index == currentStepIndex

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(36.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> SuccessGreen
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.outline)
                                )
                            }
                        }

                        if (index < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(26.dp)
                                    .background(
                                        if (index < currentStepIndex) SuccessGreen
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.padding(bottom = if (index < steps.size - 1) 14.dp else 0.dp)) {
                        Text(
                            text = pair.second,
                            fontWeight = if (isCurrent) FontWeight.ExtraBold else if (isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                            color = when {
                                isCurrent -> MaterialTheme.colorScheme.primary
                                isCompleted -> MaterialTheme.colorScheme.onSurface
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontSize = 14.sp
                        )
                        if (isCurrent) {
                            Text(
                                text = "Current Status",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
