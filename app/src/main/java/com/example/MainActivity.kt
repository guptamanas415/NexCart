package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.NexCartBottomBar
import com.example.ui.components.NexCartTopBar
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderDetailScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.NexCartTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NexCartViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NexCartViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()

            val isDark = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            NexCartTheme(darkTheme = isDark) {
                NexCartApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NexCartApp(viewModel: NexCartViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlist by viewModel.wishlistProducts.collectAsState()
    val orders by viewModel.orders.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Listen to toast messages and show Snackbar
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Android System Back Button Handler
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(AppScreen.HOME)
        }
    }

    val primaryTabs = listOf(
        AppScreen.HOME,
        AppScreen.CATEGORIES,
        AppScreen.CART,
        AppScreen.ORDERS,
        AppScreen.ACCOUNT
    )

    val showBottomBar = currentScreen in primaryTabs

    val screenTitle = when (currentScreen) {
        AppScreen.SEARCH -> "Search"
        AppScreen.PRODUCT_DETAIL -> "Product Details"
        AppScreen.CHECKOUT -> "Checkout"
        AppScreen.WISHLIST -> "My Wishlist"
        AppScreen.ORDER_DETAIL -> "Order Status"
        AppScreen.ADMIN -> "Admin Dashboard"
        AppScreen.SETTINGS -> "Settings"
        else -> null
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            NexCartTopBar(
                currentScreen = currentScreen,
                cartItemCount = cartItems.sumOf { it.quantity },
                wishlistCount = wishlist.size,
                onNavigate = { screen -> viewModel.navigateTo(screen) },
                onBackClick = {
                    if (!viewModel.navigateBack()) {
                        viewModel.navigateTo(AppScreen.HOME)
                    }
                },
                title = screenTitle
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NexCartBottomBar(
                    currentScreen = currentScreen,
                    cartCount = cartItems.sumOf { it.quantity },
                    ordersCount = orders.count { it.status != "Delivered" && it.status != "Cancelled" },
                    onTabSelected = { tab -> viewModel.navigateTo(tab) }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                AppScreen.CATEGORIES -> CategoriesScreen(viewModel = viewModel)
                AppScreen.CART -> CartScreen(viewModel = viewModel)
                AppScreen.ORDERS -> OrdersScreen(viewModel = viewModel)
                AppScreen.ACCOUNT -> AccountScreen(viewModel = viewModel)
                AppScreen.SEARCH -> SearchScreen(viewModel = viewModel)
                AppScreen.PRODUCT_DETAIL -> ProductDetailScreen(viewModel = viewModel)
                AppScreen.CHECKOUT -> CheckoutScreen(viewModel = viewModel)
                AppScreen.WISHLIST -> WishlistScreen(viewModel = viewModel)
                AppScreen.ORDER_DETAIL -> OrderDetailScreen(viewModel = viewModel)
                AppScreen.ADMIN -> AdminScreen(viewModel = viewModel)
                AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                AppScreen.AUTH -> AccountScreen(viewModel = viewModel)
            }
        }
    }
}
