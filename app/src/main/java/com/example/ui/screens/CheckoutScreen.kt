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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NexCartBlue
import com.example.ui.viewmodel.NexCartViewModel

@Composable
fun CheckoutScreen(
    viewModel: NexCartViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.cartSubtotal.collectAsState()
    val shipping by viewModel.cartShipping.collectAsState()
    val discount by viewModel.cartDiscount.collectAsState()
    val total by viewModel.cartTotal.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pinCode by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash on Delivery") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Pre-fill profile info
    LaunchedEffect(userProfile) {
        userProfile?.let { prof ->
            if (name.isBlank()) name = prof.name
            if (phone.isBlank()) phone = prof.phone
            if (email.isBlank()) email = prof.email
            if (address.isBlank()) address = prof.address
            if (city.isBlank()) city = prof.city
            if (state.isBlank()) state = prof.state
            if (pinCode.isBlank()) pinCode = prof.pinCode
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("checkout_screen")
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Delivery Address Card
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
                                text = "Shipping Address",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name *") },
                            modifier = Modifier.fillMaxWidth().testTag("checkout_name_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Phone Number *") },
                                modifier = Modifier.weight(1f).testTag("checkout_phone_input"),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email *") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Street Address *") },
                            modifier = Modifier.fillMaxWidth().testTag("checkout_address_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City *") },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = state,
                                onValueChange = { state = it },
                                label = { Text("State *") },
                                modifier = Modifier.weight(0.9f),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = pinCode,
                                onValueChange = { pinCode = it },
                                label = { Text("PIN *") },
                                modifier = Modifier.weight(0.9f),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // Payment Methods Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = NexCartBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Payment Method",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Cash on Delivery
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { paymentMethod = "Cash on Delivery" }
                                .background(
                                    if (paymentMethod == "Cash on Delivery")
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    else Color.Transparent
                                )
                                .padding(10.dp)
                        ) {
                            RadioButton(
                                selected = paymentMethod == "Cash on Delivery",
                                onClick = { paymentMethod = "Cash on Delivery" },
                                colors = RadioButtonDefaults.colors(selectedColor = NexCartBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Cash on Delivery", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Pay with cash upon receipt", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Demo Online Payment
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { paymentMethod = "Demo Online Payment" }
                                .background(
                                    if (paymentMethod == "Demo Online Payment")
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    else Color.Transparent
                                )
                                .padding(10.dp)
                        ) {
                            RadioButton(
                                selected = paymentMethod == "Demo Online Payment",
                                onClick = { paymentMethod = "Demo Online Payment" },
                                colors = RadioButtonDefaults.colors(selectedColor = NexCartBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Demo / Test Online Payment", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Simulated instant card/wallet confirmation", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Products in this Order
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Order Items (${cartItems.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.product.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Qty: ${item.quantity} × ${viewModel.formatPrice(item.product.price)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = viewModel.formatPrice(item.product.price * item.quantity),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Payable", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                text = viewModel.formatPrice(total),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Error display
            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }

        // Place Order Sticky Footer
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Encrypted demo checkout",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank() || address.isBlank() || city.isBlank() || pinCode.isBlank()) {
                            errorMessage = "Please fill in all required shipping address fields."
                            return@Button
                        }
                        errorMessage = null
                        viewModel.placeOrder(
                            customerName = name,
                            customerEmail = email,
                            customerPhone = phone,
                            address = address,
                            city = city,
                            state = state,
                            pinCode = pinCode,
                            paymentMethod = paymentMethod
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("place_order_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexCartBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Place Order • ${viewModel.formatPrice(total)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
