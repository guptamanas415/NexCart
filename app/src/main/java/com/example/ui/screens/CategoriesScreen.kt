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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProductCard
import com.example.ui.viewmodel.NexCartViewModel
import com.example.ui.viewmodel.SortOption

@Composable
fun CategoriesScreen(
    viewModel: NexCartViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val sortOption by viewModel.selectedSortOption.collectAsState()
    val inStockOnly by viewModel.inStockOnlyFilter.collectAsState()
    val wishlist by viewModel.wishlistProducts.collectAsState()
    val wishIds = wishlist.map { it.id }.toSet()

    var showSortMenu by remember { mutableStateOf(false) }

    val allCategoryTabs = listOf("All") + categories.map { it.name }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("categories_screen")
    ) {
        // Category Horizontal Selector Pills
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(allCategoryTabs) { cat ->
                    val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { viewModel.selectedCategoryFilter.value = cat }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("category_tab_$cat"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Subheader: Sort dropdown, In-stock toggle, Results count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredProducts.size} products found",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // In-Stock Filter Chip
                FilterChip(
                    selected = inStockOnly,
                    onClick = { viewModel.inStockOnlyFilter.value = !inStockOnly },
                    label = { Text("In Stock", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Sort Dropdown Button
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { showSortMenu = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Sort",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = sortOption.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option.label,
                                        fontWeight = if (option == sortOption) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    viewModel.selectedSortOption.value = option
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Products Grid
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No products found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try adjusting your filters or category selection.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = wishIds.contains(product.id),
                        onCardClick = { viewModel.openProductDetail(product.id) },
                        onAddToCart = { viewModel.addToCart(product) },
                        onToggleWishlist = { viewModel.toggleWishlist(product) },
                        formatPrice = { viewModel.formatPrice(it) }
                    )
                }
            }
        }
    }
}
