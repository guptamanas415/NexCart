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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItemWithProduct
import com.example.ui.components.ProductImage
import com.example.ui.theme.NexCartBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NexCartViewModel

@Composable
fun CartScreen(
    viewModel: NexCartViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val shipping by viewModel.cartShipping.collectAsState()
    val discount by viewModel.cartDiscount.collectAsState()
    val total by viewModel.cartTotal.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()

    var couponInput by remember { mutableStateOf("") }

    if (cartItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp)
                .testTag("cart_empty_state"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Your Cart is Empty",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Looks like you haven't added anything to your cart yet. Discover trending deals today!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NexCartBlue)
                ) {
                    Text("Start Shopping", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("cart_screen")
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Cart Items List
            items(cartItems, key = { it.product.id }) { item ->
                CartItemRow(
                    item = item,
                    onIncrease = { viewModel.updateCartQuantity(item.product.id, item.quantity + 1) },
                    onDecrease = { viewModel.updateCartQuantity(item.product.id, item.quantity - 1) },
                    onRemove = { viewModel.removeFromCart(item.product.id, item.product.name) },
                    onProductClick = { viewModel.openProductDetail(item.product.id) },
                    formatPrice = { viewModel.formatPrice(it) }
                )
            }

            // Coupon Code Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = NexCartBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Have a Promo Code?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "Try: NEXCART10",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (appliedCoupon != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SuccessGreen.copy(alpha = 0.12f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Code '$appliedCoupon' Applied",
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen,
                                        fontSize = 13.sp
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.removeCoupon() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove coupon",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it },
                                    placeholder = { Text("Enter code (e.g. NEXCART10)", fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NexCartBlue
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (viewModel.applyCoupon(couponInput)) {
                                            couponInput = ""
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NexCartBlue)
                                ) {
                                    Text("Apply", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Order Price Summary
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Order Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        SummaryRow("Subtotal", viewModel.formatPrice(subtotal))
                        SummaryRow(
                            "Shipping",
                            if (shipping == 0.0) "FREE" else viewModel.formatPrice(shipping),
                            isHighlight = shipping == 0.0
                        )
                        if (discount > 0.0) {
                            SummaryRow(
                                "Discount (${appliedCoupon ?: ""})",
                                "-${viewModel.formatPrice(discount)}",
                                isGreen = true
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = viewModel.formatPrice(total),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (shipping > 0.0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tip: Add items worth ${viewModel.formatPrice((499.0 - subtotal).coerceAtLeast(0.0))} more for FREE shipping!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Checkout Button Footer
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = { viewModel.navigateTo(AppScreen.CHECKOUT) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp)
                    .testTag("cart_checkout_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NexCartBlue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Proceed to Checkout (${viewModel.formatPrice(total)})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItemWithProduct,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    onProductClick: () -> Unit,
    formatPrice: (Double) -> String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onProductClick)
            ) {
                ProductImage(
                    imageUrl = item.product.imageUrl,
                    contentDescription = item.product.name,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(onClick = onProductClick)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.product.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatPrice(item.product.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Quantity buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable(onClick = onDecrease),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "${item.quantity}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable(onClick = onIncrease),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Remove Button
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    isGreen: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight || isGreen) FontWeight.Bold else FontWeight.Medium,
            color = when {
                isGreen -> SuccessGreen
                isHighlight -> NexCartBlue
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
