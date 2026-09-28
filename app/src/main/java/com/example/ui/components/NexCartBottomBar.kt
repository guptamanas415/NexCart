package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppScreen

private data class NavItem(
    val screen: AppScreen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun NexCartBottomBar(
    currentScreen: AppScreen,
    cartCount: Int,
    ordersCount: Int,
    onTabSelected: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(AppScreen.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home, "bottom_nav_home"),
        NavItem(AppScreen.CATEGORIES, "Categories", Icons.Filled.Category, Icons.Outlined.Category, "bottom_nav_categories"),
        NavItem(AppScreen.CART, "Cart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart, "bottom_nav_cart"),
        NavItem(AppScreen.ORDERS, "Orders", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong, "bottom_nav_orders"),
        NavItem(AppScreen.ACCOUNT, "Account", Icons.Filled.Person, Icons.Outlined.Person, "bottom_nav_account")
    )

    NavigationBar(
        modifier = modifier,
        windowInsets = WindowInsets.navigationBars,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        items.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.screen) },
                icon = {
                    if (item.screen == AppScreen.CART && cartCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ) {
                                    Text("$cartCount", fontSize = 10.sp)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        }
                    } else if (item.screen == AppScreen.ORDERS && ordersCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = Color.White
                                ) {
                                    Text("$ordersCount", fontSize = 10.sp)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}
