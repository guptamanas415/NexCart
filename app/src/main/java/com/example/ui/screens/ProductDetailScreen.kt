package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.components.ProductCard
import com.example.ui.components.ProductImage
import com.example.ui.theme.NexCartBlue
import com.example.ui.theme.NexCartPurple
import com.example.ui.theme.StarGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NexCartViewModel

@Composable
fun ProductDetailScreen(
    viewModel: NexCartViewModel,
    modifier: Modifier = Modifier
) {
    val selectedProductId by viewModel.selectedProductId.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val wishlist by viewModel.wishlistProducts.collectAsState()
    val wishIds = wishlist.map { it.id }.toSet()

    val product = allProducts.firstOrNull { it.id == selectedProductId }

    if (product == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Product not found", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.navigateBack() }) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    val isWishlisted = wishIds.contains(product.id)
    var selectedQuantity by remember { mutableIntStateOf(1) }

    val relatedProducts = allProducts.filter { it.category == product.category && it.id != product.id }.take(6)

    Column(modifier = modifier.fillMaxSize().testTag("product_detail_screen")) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Large Product Image with Badges
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    ProductImage(
                        imageUrl = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Wishlist toggle icon
                    IconButton(
                        onClick = { viewModel.toggleWishlist(product) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), CircleShape)
                            .testTag("detail_wishlist_button")
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Discount badge
                    if (product.discountPercent > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.error)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "SAVE ${product.discountPercent}%",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Product Information Header
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Category & Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.category.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        if (product.badge.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = product.badge,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Title
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating & Reviews
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = StarGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${product.rating}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${product.ratingCount} reviews)",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        // Stock Indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (product.stock > 0) SuccessGreen else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (product.stock > 0) "In Stock (${product.stock} available)" else "Out of Stock",
                                color = if (product.stock > 0) SuccessGreen else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Price Section
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = viewModel.formatPrice(product.price),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (product.oldPrice > product.price) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = viewModel.formatPrice(product.oldPrice),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quantity Selector
                    Text(
                        text = "Quantity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = { if (selectedQuantity > 1) selectedQuantity-- },
                            enabled = selectedQuantity > 1,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text(
                            text = "$selectedQuantity",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                        IconButton(
                            onClick = { if (selectedQuantity < product.stock) selectedQuantity++ },
                            enabled = selectedQuantity < product.stock,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
            }

            // Related Products
            if (relatedProducts.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        Text(
                            text = "Related Products",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(relatedProducts) { related ->
                                ProductCard(
                                    product = related,
                                    isWishlisted = wishIds.contains(related.id),
                                    onCardClick = { viewModel.openProductDetail(related.id) },
                                    onAddToCart = { viewModel.addToCart(related) },
                                    onToggleWishlist = { viewModel.toggleWishlist(related) },
                                    formatPrice = { viewModel.formatPrice(it) },
                                    modifier = Modifier.width(170.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Action Bar: Add to Cart and Buy Now
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.addToCart(product, selectedQuantity)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("detail_add_to_cart_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to Cart", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        viewModel.addToCart(product, selectedQuantity)
                        viewModel.navigateTo(AppScreen.CHECKOUT)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("detail_buy_now_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexCartBlue,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Buy Now", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
