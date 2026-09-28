package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexCartTopBar(
    currentScreen: AppScreen,
    cartItemCount: Int,
    wishlistCount: Int,
    onNavigate: (AppScreen) -> Unit,
    onBackClick: () -> Unit,
    title: String? = null,
    modifier: Modifier = Modifier
) {
    val isPrimaryScreen = currentScreen in listOf(
        AppScreen.HOME,
        AppScreen.CATEGORIES,
        AppScreen.CART,
        AppScreen.ORDERS,
        AppScreen.ACCOUNT
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isPrimaryScreen) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("topbar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title ?: currentScreen.name.replace("_", " "),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            } else {
                // Brand logo on primary tabs
                NexCartLogo(
                    modifier = Modifier
                        .clickable { onNavigate(AppScreen.HOME) }
                        .padding(vertical = 4.dp),
                    iconSize = 32.dp,
                    showTagline = false
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            // Top action buttons (Search, Wishlist, Cart, Account/Admin)
            if (currentScreen != AppScreen.SEARCH) {
                IconButton(
                    onClick = { onNavigate(AppScreen.SEARCH) },
                    modifier = Modifier.testTag("topbar_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Wishlist icon with badge
            IconButton(
                onClick = { onNavigate(AppScreen.WISHLIST) },
                modifier = Modifier.testTag("topbar_wishlist_button")
            ) {
                BadgedBox(
                    badge = {
                        if (wishlistCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = Color.White
                            ) {
                                Text("$wishlistCount", fontSize = 10.sp)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (wishlistCount > 0) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (wishlistCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Cart icon with badge (only show if not already on cart screen)
            if (currentScreen != AppScreen.CART) {
                IconButton(
                    onClick = { onNavigate(AppScreen.CART) },
                    modifier = Modifier.testTag("topbar_cart_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ) {
                                    Text("$cartItemCount", fontSize = 10.sp)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Admin toggle chip/button
            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (currentScreen == AppScreen.ADMIN)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    )
                    .clickable {
                        if (currentScreen == AppScreen.ADMIN) onNavigate(AppScreen.HOME)
                        else onNavigate(AppScreen.ADMIN)
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("topbar_admin_toggle")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin",
                        tint = if (currentScreen == AppScreen.ADMIN) Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (currentScreen == AppScreen.ADMIN) "Exit" else "Admin",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentScreen == AppScreen.ADMIN) Color.White else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
