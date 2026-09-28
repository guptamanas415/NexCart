package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.ProductCard
import com.example.ui.theme.NexCartBlue
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NexCartViewModel

@Composable
fun WishlistScreen(
    viewModel: NexCartViewModel,
    modifier: Modifier = Modifier
) {
    val wishlistProducts by viewModel.wishlistProducts.collectAsState()

    if (wishlistProducts.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp)
                .testTag("wishlist_empty_state"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(42.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Your Wishlist is Empty",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Save items you love so you can easily find and buy them later.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NexCartBlue)
                ) {
                    Text("Explore Products")
                }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("wishlist_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${wishlistProducts.size} Saved Items",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(wishlistProducts, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    isWishlisted = true,
                    onCardClick = { viewModel.openProductDetail(product.id) },
                    onAddToCart = { viewModel.addToCart(product) },
                    onToggleWishlist = { viewModel.toggleWishlist(product) },
                    formatPrice = { viewModel.formatPrice(it) }
                )
            }
        }
    }
}
