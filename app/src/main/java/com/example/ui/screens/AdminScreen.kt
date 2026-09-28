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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.DemoCustomer
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.ProductEntity
import com.example.ui.components.StatCard
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NexCartBlue
import com.example.ui.theme.NexCartPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.NexCartViewModel

enum class AdminTab(val label: String) {
    OVERVIEW("Overview"),
    PRODUCTS("Products"),
    ORDERS("Orders"),
    CATEGORIES("Categories"),
    CUSTOMERS("Customers")
}

@Composable
fun AdminScreen(
    viewModel: NexCartViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AdminTab.OVERVIEW) }

    val allProducts by viewModel.allProducts.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val customers = viewModel.demoCustomers

    // Modals
    var productToEdit by remember { mutableStateOf<ProductEntity?>(null) }
    var isCreatingNewProduct by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_screen")
    ) {
        // Admin Tab Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(AdminTab.entries) { tab ->
                val isSelected = tab == selectedTab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.label,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            AdminTab.OVERVIEW -> {
                AdminOverviewTab(
                    products = allProducts,
                    orders = orders,
                    customers = customers,
                    formatPrice = { viewModel.formatPrice(it) }
                )
            }
            AdminTab.PRODUCTS -> {
                AdminProductsTab(
                    products = allProducts,
                    onAddProduct = { isCreatingNewProduct = true },
                    onEditProduct = { productToEdit = it },
                    onDeleteProduct = { productToDelete = it },
                    onToggleActive = { p -> viewModel.saveProduct(p.copy(isActive = !p.isActive), false) },
                    formatPrice = { viewModel.formatPrice(it) }
                )
            }
            AdminTab.ORDERS -> {
                AdminOrdersTab(
                    orders = orders,
                    onStatusChange = { orderId, newStatus ->
                        viewModel.updateOrderStatus(orderId, newStatus)
                    },
                    formatPrice = { viewModel.formatPrice(it) }
                )
            }
            AdminTab.CATEGORIES -> {
                AdminCategoriesTab(
                    categories = categories,
                    onAddCategory = { showAddCategoryDialog = true },
                    onDeleteCategory = { viewModel.deleteCategory(it) }
                )
            }
            AdminTab.CUSTOMERS -> {
                AdminCustomersTab(customers = customers)
            }
        }
    }

    // Edit / Create Product Dialog
    if (isCreatingNewProduct || productToEdit != null) {
        val editing = productToEdit
        ProductEditDialog(
            product = editing,
            categories = categories,
            onDismiss = {
                isCreatingNewProduct = false
                productToEdit = null
            },
            onSave = { savedProduct, isNew ->
                viewModel.saveProduct(savedProduct, isNew)
                isCreatingNewProduct = false
                productToEdit = null
            }
        )
    }

    // Delete Product Confirmation Dialog
    if (productToDelete != null) {
        val p = productToDelete!!
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete Product", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete '${p.name}' from your store inventory?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(p)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Category Dialog
    if (showAddCategoryDialog) {
        var newCatName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add Category", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newCatName,
                    onValueChange = { newCatName = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addCategory(newCatName)
                        showAddCategoryDialog = false
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AdminOverviewTab(
    products: List<ProductEntity>,
    orders: List<OrderEntity>,
    customers: List<DemoCustomer>,
    formatPrice: (Double) -> String
) {
    val totalSales = orders.sumOf { it.total }
    val pendingCount = orders.count { it.status.equals("Pending", ignoreCase = true) }
    val deliveredCount = orders.count { it.status.equals("Delivered", ignoreCase = true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Dashboard Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Live store performance metrics and operational status",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            StatCard(
                title = "Total Gross Sales",
                value = formatPrice(totalSales),
                icon = Icons.Default.AttachMoney,
                accentColor = SuccessGreen
            )
        }

        item {
            StatCard(
                title = "Total Orders",
                value = "${orders.size}",
                icon = Icons.Default.ReceiptLong,
                accentColor = NexCartBlue
            )
        }

        item {
            StatCard(
                title = "Pending Fulfillment",
                value = "$pendingCount",
                icon = Icons.Default.Pending,
                accentColor = WarningAmber
            )
        }

        item {
            StatCard(
                title = "Delivered Orders",
                value = "$deliveredCount",
                icon = Icons.Default.CheckCircle,
                accentColor = NexCartPurple
            )
        }

        item {
            StatCard(
                title = "Catalog Products",
                value = "${products.size}",
                icon = Icons.Default.Inventory,
                accentColor = NexCartBlue
            )
        }

        item {
            StatCard(
                title = "Active Customers",
                value = "${customers.size}",
                icon = Icons.Default.People,
                accentColor = Color(0xFF0EA5E9)
            )
        }
    }
}

@Composable
private fun AdminProductsTab(
    products: List<ProductEntity>,
    onAddProduct: () -> Unit,
    onEditProduct: (ProductEntity) -> Unit,
    onDeleteProduct: (ProductEntity) -> Unit,
    onToggleActive: (ProductEntity) -> Unit,
    formatPrice: (Double) -> String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inventory (${products.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onAddProduct,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NexCartBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Product", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        items(products, key = { it.id }) { product ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = product.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (!product.isActive) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.errorContainer)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("Inactive", fontSize = 10.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${product.category} • Stock: ${product.stock} • ${formatPrice(product.price)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = product.isActive,
                        onCheckedChange = { onToggleActive(product) },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = SwitchDefaults.colors(checkedThumbColor = NexCartBlue)
                    )

                    IconButton(onClick = { onEditProduct(product) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NexCartBlue, modifier = Modifier.size(18.dp))
                    }

                    IconButton(onClick = { onDeleteProduct(product) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminOrdersTab(
    orders: List<OrderEntity>,
    onStatusChange: (orderId: String, status: String) -> Unit,
    formatPrice: (Double) -> String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Order Management (${orders.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(orders, key = { it.orderId }) { order ->
            var expandedDropdown by remember { mutableStateOf(false) }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "#${order.orderId}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )

                        // Status Dropdown Button
                        Box {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .clickable { expandedDropdown = true }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${order.status} ▾",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            DropdownMenu(
                                expanded = expandedDropdown,
                                onDismissRequest = { expandedDropdown = false }
                            ) {
                                OrderStatus.entries.forEach { statusOption ->
                                    DropdownMenuItem(
                                        text = { Text(statusOption.label) },
                                        onClick = {
                                            onStatusChange(order.orderId, statusOption.label)
                                            expandedDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Customer: ${order.customerName} (${order.customerPhone})",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Items: ${order.itemsSummary}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Payment: ${order.paymentMethod}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatPrice(order.total),
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminCategoriesTab(
    categories: List<CategoryEntity>,
    onAddCategory: () -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Store Categories (${categories.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onAddCategory,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NexCartBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Category", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        items(categories, key = { it.id }) { cat ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Category, contentDescription = null, tint = NexCartBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = cat.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    IconButton(onClick = { onDeleteCategory(cat) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminCustomersTab(customers: List<DemoCustomer>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Registered Customers (${customers.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(customers, key = { it.id }) { customer ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NexCartBlue.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = customer.name.take(1),
                            color = NexCartBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = customer.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "${customer.email} • ${customer.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Orders: ${customer.orderCount} • Joined: ${customer.registeredDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductEditDialog(
    product: ProductEntity?,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (product: ProductEntity, isNew: Boolean) -> Unit
) {
    val isNew = product == null
    var name by remember { mutableStateOf(product?.name ?: "") }
    var category by remember { mutableStateOf(product?.category ?: (categories.firstOrNull()?.name ?: "Electronics")) }
    var priceStr by remember { mutableStateOf(product?.price?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "1499") }
    var oldPriceStr by remember { mutableStateOf(product?.oldPrice?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "1999") }
    var stockStr by remember { mutableStateOf(product?.stock?.toString() ?: "20") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var badge by remember { mutableStateOf(product?.badge ?: "Featured") }
    var imageUrl by remember { mutableStateOf(product?.imageUrl ?: "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "Add New Product" else "Edit Product", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row {
                        OutlinedTextField(
                            value = priceStr,
                            onValueChange = { priceStr = it },
                            label = { Text("Price (₹)") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = oldPriceStr,
                            onValueChange = { oldPriceStr = it },
                            label = { Text("Old Price (₹)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row {
                        OutlinedTextField(
                            value = stockStr,
                            onValueChange = { stockStr = it },
                            label = { Text("Stock Qty") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = badge,
                            onValueChange = { badge = it },
                            label = { Text("Badge Label") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("Image URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceStr.toDoubleOrNull() ?: 1499.0
                    val oldP = oldPriceStr.toDoubleOrNull() ?: (p * 1.25)
                    val st = stockStr.toIntOrNull() ?: 10
                    val disc = if (oldP > p) (((oldP - p) / oldP) * 100).toInt() else 0

                    val resultProduct = ProductEntity(
                        id = product?.id ?: System.currentTimeMillis(),
                        name = name.ifBlank { "Sample Product" },
                        category = category.ifBlank { "Electronics" },
                        description = description.ifBlank { "High-grade product built for everyday performance and style." },
                        price = p,
                        oldPrice = oldP,
                        discountPercent = disc,
                        rating = product?.rating ?: 4.8f,
                        ratingCount = product?.ratingCount ?: 15,
                        stock = st,
                        imageUrl = imageUrl,
                        badge = badge,
                        isTrending = product?.isTrending ?: true,
                        isBestSeller = product?.isBestSeller ?: false,
                        isNewArrival = product?.isNewArrival ?: true,
                        isRecommended = product?.isRecommended ?: true,
                        isActive = product?.isActive ?: true
                    )
                    onSave(resultProduct, isNew)
                }
            ) {
                Text(if (isNew) "Add to Store" else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
