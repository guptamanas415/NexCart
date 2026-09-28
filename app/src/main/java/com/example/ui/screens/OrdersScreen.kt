package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.ui.components.OrderTimeline
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NexCartBlue
import com.example.ui.theme.NexCartPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NexCartViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(
    viewModel: NexCartViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()

    if (orders.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp)
                .testTag("orders_empty_state"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Orders Yet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Once you place an order, you can track delivery and status right here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                    Text("Explore Products")
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("orders_list_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(orders, key = { it.orderId }) { order ->
            OrderCard(
                order = order,
                onClick = { viewModel.openOrderDetail(order.orderId) },
                formatPrice = { viewModel.formatPrice(it) }
            )
        }
    }
}

@Composable
fun OrderCard(
    order: OrderEntity,
    onClick: () -> Unit,
    formatPrice: (Double) -> String
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(order.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("order_card_${order.orderId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Order ID & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.orderId}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            // Items Summary
            Text(
                text = order.itemsSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Payment & Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = order.paymentMethod,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatPrice(order.total),
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Details",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: String) {
    val (bgColor, textColor) = when (OrderStatus.fromString(status)) {
        OrderStatus.PENDING -> WarningAmber.copy(alpha = 0.15f) to WarningAmber
        OrderStatus.CONFIRMED -> NexCartBlue.copy(alpha = 0.15f) to NexCartBlue
        OrderStatus.PACKED -> NexCartPurple.copy(alpha = 0.15f) to NexCartPurple
        OrderStatus.SHIPPED -> NexCartBlue.copy(alpha = 0.15f) to NexCartBlue
        OrderStatus.DELIVERED -> SuccessGreen.copy(alpha = 0.15f) to SuccessGreen
        OrderStatus.CANCELLED -> ErrorRed.copy(alpha = 0.15f) to ErrorRed
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = textColor
        )
    }
}

@Composable
fun OrderDetailScreen(
    viewModel: NexCartViewModel,
    modifier: Modifier = Modifier
) {
    val selectedOrderId by viewModel.selectedOrderId.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val order = orders.firstOrNull { it.orderId == selectedOrderId }

    if (order == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Order details not found", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.navigateBack() }) {
                    Text("Back to Orders")
                }
            }
        }
        return
    }

    val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy • hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(order.date))

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("order_detail_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status & ID Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Order #${order.orderId}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        OrderStatusBadge(status = order.status)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Placed on $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Live Order Timeline
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tracking Timeline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OrderTimeline(currentStatus = order.status)
                }
            }
        }

        // Delivery Info
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = NexCartBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Delivery Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = order.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = "${order.address}\n${order.city}, ${order.state} - ${order.pinCode}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Phone: ${order.customerPhone} • Email: ${order.customerEmail}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Items and Total breakdown
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Items & Payment Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = order.itemsSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(viewModel.formatPrice(order.subtotal))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Shipping", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(if (order.shipping == 0.0) "FREE" else viewModel.formatPrice(order.shipping))
                    }
                    if (order.discount > 0.0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Discount Applied", color = SuccessGreen)
                            Text("-${viewModel.formatPrice(order.discount)}", color = SuccessGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Paid", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = viewModel.formatPrice(order.total),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Payment Method: ${order.paymentMethod}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
