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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfileEntity
import com.example.ui.theme.NexCartBlue
import com.example.ui.theme.NexCartPurple
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NexCartViewModel

@Composable
fun AccountScreen(
    viewModel: NexCartViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val wishlist by viewModel.wishlistProducts.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }

    val profile = userProfile ?: UserProfileEntity()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("account_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar Monogram
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(NexCartBlue, NexCartPurple)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.name.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 26.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = profile.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = profile.phone,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier.testTag("edit_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = NexCartBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Saved Address Summary
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Default Shipping Address",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${profile.address}, ${profile.city}, ${profile.state} ${profile.pinCode}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Account Quick Links Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    AccountNavRow(
                        title = "My Orders",
                        subtitle = "${orders.size} orders placed",
                        icon = Icons.Default.ReceiptLong,
                        onClick = { viewModel.navigateTo(AppScreen.ORDERS) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    AccountNavRow(
                        title = "My Wishlist",
                        subtitle = "${wishlist.size} saved items",
                        icon = Icons.Default.Favorite,
                        onClick = { viewModel.navigateTo(AppScreen.WISHLIST) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    AccountNavRow(
                        title = "App Settings",
                        subtitle = "Theme, Currency, Notifications",
                        icon = Icons.Default.Settings,
                        onClick = { viewModel.navigateTo(AppScreen.SETTINGS) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    AccountNavRow(
                        title = "Admin Dashboard",
                        subtitle = "Manage inventory, orders & categories",
                        icon = Icons.Default.AdminPanelSettings,
                        iconTint = NexCartPurple,
                        onClick = { viewModel.navigateTo(AppScreen.ADMIN) }
                    )
                }
            }
        }

        // Authentication & Session
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = NexCartBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Authentication & Account",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (profile.isLoggedIn) "Logged in as ${profile.name} (${profile.email})"
                        else "Currently browsing as Guest",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (profile.isLoggedIn) {
                        OutlinedButton(
                            onClick = { viewModel.setLoginStatus(false) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Logout", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { showAuthDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NexCartBlue)
                        ) {
                            Text("Login / Register Demo Account", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentProfile = profile,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                viewModel.saveUserProfile(updated)
                showEditProfileDialog = false
            }
        )
    }

    // Auth Dialog
    if (showAuthDialog) {
        AuthDialog(
            onDismiss = { showAuthDialog = false },
            onSuccess = { name, email, phone ->
                viewModel.saveUserProfile(
                    profile.copy(
                        name = name,
                        email = email,
                        phone = phone,
                        isLoggedIn = true
                    )
                )
                showAuthDialog = false
            }
        )
    }
}

@Composable
private fun AccountNavRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = NexCartBlue,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun EditProfileDialog(
    currentProfile: UserProfileEntity,
    onDismiss: () -> Unit,
    onSave: (UserProfileEntity) -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.name) }
    var email by remember { mutableStateOf(currentProfile.email) }
    var phone by remember { mutableStateOf(currentProfile.phone) }
    var address by remember { mutableStateOf(currentProfile.address) }
    var city by remember { mutableStateOf(currentProfile.city) }
    var state by remember { mutableStateOf(currentProfile.state) }
    var pinCode by remember { mutableStateOf(currentProfile.pinCode) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile & Address", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Street Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City") },
                            modifier = Modifier.weight(1.2f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = state,
                            onValueChange = { state = it },
                            label = { Text("State") },
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = pinCode,
                        onValueChange = { pinCode = it },
                        label = { Text("PIN Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        currentProfile.copy(
                            name = name,
                            email = email,
                            phone = phone,
                            address = address,
                            city = city,
                            state = state,
                            pinCode = pinCode
                        )
                    )
                }
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AuthDialog(
    onDismiss: () -> Unit,
    onSuccess: (name: String, email: String, phone: String) -> Unit
) {
    var isRegisterTab by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("demo.user@nexcart.com") }
    var phone by remember { mutableStateOf("+1 (555) 302-8819") }
    var password by remember { mutableStateOf("password123") }
    var confirmPassword by remember { mutableStateOf("password123") }
    var authError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isRegisterTab) "Create Demo Account" else "NexCart Demo Login",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isRegisterTab) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { isRegisterTab = false }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sign In", fontWeight = if (!isRegisterTab) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isRegisterTab) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { isRegisterTab = true }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Register", fontWeight = if (isRegisterTab) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp)
                    }
                }

                if (isRegisterTab) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isRegisterTab) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (authError != null) {
                    Text(
                        text = authError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = "Demo Mode: Any credentials will simulate instant authenticated session.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isRegisterTab && password != confirmPassword) {
                        authError = "Passwords do not match."
                        return@Button
                    }
                    val finalName = if (isRegisterTab && name.isNotBlank()) name else "Alex Morgan"
                    onSuccess(finalName, email, phone)
                }
            ) {
                Text(if (isRegisterTab) "Register & Sign In" else "Sign In")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
