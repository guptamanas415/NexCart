package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.ui.components.ProductCard
import com.example.ui.theme.NexCartBlue
import com.example.ui.theme.NexCartPurple
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NexCartViewModel

@Composable
fun HomeScreen(
    viewModel: NexCartViewModel,
    modifier: Modifier = Modifier
) {
    val activeProducts by viewModel.activeProducts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val wishlist by viewModel.wishlistProducts.collectAsState()
    val wishIds = wishlist.map { it.id }.toSet()

    val trendingProducts = activeProducts.filter { it.isTrending }
    val bestSellers = activeProducts.filter { it.isBestSeller }
    val newArrivals = activeProducts.filter { it.isNewArrival }
    val recommended = activeProducts.filter { it.isRecommended }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Promotional Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(200.dp)
                    .testTag("hero_banner"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = "Hero banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay for contrast
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xEE1E1B4B),
                                        Color(0x992563EB),
                                        Color(0x33000000)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NexCartPurple)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "SPECIAL OFFER",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Everything you need,\nin one place.",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.openCategory("All") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = NexCartBlue
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("hero_shop_now_button")
                        ) {
                            Text(
                                text = "Shop Now",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Categories section
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { viewModel.navigateTo(AppScreen.CATEGORIES) }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(categories) { category ->
                        CategoryItem(
                            category = category,
                            onClick = { viewModel.openCategory(category.name) }
                        )
                    }
                }
            }
        }

        // Trending Section
        item {
            SectionHeader(
                title = "Trending Now",
                icon = Icons.Default.LocalFireDepartment,
                iconTint = Color(0xFFEF4444),
                onSeeAll = { viewModel.openCategory("All") }
            )
            HorizontalProductRow(
                products = trendingProducts,
                wishIds = wishIds,
                viewModel = viewModel
            )
        }

        // Best Sellers Section
        item {
            SectionHeader(
                title = "Best Sellers",
                icon = Icons.Default.Loyalty,
                iconTint = NexCartBlue,
                onSeeAll = { viewModel.openCategory("All") }
            )
            HorizontalProductRow(
                products = bestSellers,
                wishIds = wishIds,
                viewModel = viewModel
            )
        }

        // New Arrivals Section
        item {
            SectionHeader(
                title = "New Arrivals",
                icon = Icons.Default.Bolt,
                iconTint = Color(0xFFF59E0B),
                onSeeAll = { viewModel.openCategory("All") }
            )
            HorizontalProductRow(
                products = newArrivals,
                wishIds = wishIds,
                viewModel = viewModel
            )
        }

        // Recommended Section
        item {
            SectionHeader(
                title = "Recommended For You",
                icon = Icons.Default.Star,
                iconTint = NexCartPurple,
                onSeeAll = { viewModel.openCategory("All") }
            )
            HorizontalProductRow(
                products = recommended,
                wishIds = wishIds,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    onSeeAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Text(
            text = "View All",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable(onClick = onSeeAll)
                .padding(4.dp)
        )
    }
}

@Composable
private fun HorizontalProductRow(
    products: List<ProductEntity>,
    wishIds: Set<Long>,
    viewModel: NexCartViewModel
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                isWishlisted = wishIds.contains(product.id),
                onCardClick = { viewModel.openProductDetail(product.id) },
                onAddToCart = { viewModel.addToCart(product) },
                onToggleWishlist = { viewModel.toggleWishlist(product) },
                formatPrice = { viewModel.formatPrice(it) },
                modifier = Modifier.width(180.dp)
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: CategoryEntity,
    onClick: () -> Unit
) {
    val icon = when (category.id) {
        "electronics" -> Icons.Default.Devices
        "fashion" -> Icons.Default.Checkroom
        "home" -> Icons.Default.Home
        "beauty" -> Icons.Default.Spa
        "sports" -> Icons.Default.FitnessCenter
        "accessories" -> Icons.Default.Watch
        else -> Icons.Default.Category
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = category.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
