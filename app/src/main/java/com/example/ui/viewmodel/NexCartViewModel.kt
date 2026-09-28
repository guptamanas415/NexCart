package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DemoData
import com.example.data.model.CartItemWithProduct
import com.example.data.model.CategoryEntity
import com.example.data.model.DemoCustomer
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.UserProfileEntity
import com.example.data.repository.NexCartRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

enum class AppScreen {
    HOME,
    CATEGORIES,
    CART,
    ORDERS,
    ACCOUNT,
    SEARCH,
    PRODUCT_DETAIL,
    CHECKOUT,
    WISHLIST,
    ORDER_DETAIL,
    ADMIN,
    SETTINGS,
    AUTH
}

enum class SortOption(val label: String) {
    RECOMMENDED("Recommended"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    RATING("Highest Rated"),
    NEWEST("Newest First")
}

class NexCartViewModel(application: Application) : AndroidViewModel(application) {

    val repository = NexCartRepository(application, viewModelScope)

    // Screen navigation stack
    private val screenStack = mutableListOf<AppScreen>(AppScreen.HOME)
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Navigation payloads
    private val _selectedProductId = MutableStateFlow<Long?>(null)
    val selectedProductId: StateFlow<Long?> = _selectedProductId.asStateFlow()

    private val _selectedOrderId = MutableStateFlow<String?>(null)
    val selectedOrderId: StateFlow<String?> = _selectedOrderId.asStateFlow()

    // Transient message feedback
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    // Settings
    val themeMode: StateFlow<String> = repository.themeMode
    val notificationsEnabled: StateFlow<Boolean> = repository.notificationsEnabled
    val selectedCurrency: StateFlow<String> = repository.selectedCurrency

    // Products & Categories
    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeProducts: StateFlow<List<ProductEntity>> = repository.activeProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart
    val cartItems: StateFlow<List<CartItemWithProduct>> = repository.cartItemsWithProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Wishlist
    val wishlistProducts: StateFlow<List<ProductEntity>> = repository.wishlistProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Orders
    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Profile
    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Demo Customers for Admin
    val demoCustomers: List<DemoCustomer> = DemoData.demoCustomers

    // Search and Filters
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow("All")
    val selectedSortOption = MutableStateFlow(SortOption.RECOMMENDED)
    val minPriceFilter = MutableStateFlow(0f)
    val maxPriceFilter = MutableStateFlow(150000f)
    val minRatingFilter = MutableStateFlow(0f)
    val inStockOnlyFilter = MutableStateFlow(false)

    // Applied Promo code
    val appliedCoupon = MutableStateFlow<String?>(null)

    // Filtered Products flow
    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        activeProducts,
        searchQuery,
        selectedCategoryFilter,
        selectedSortOption,
        minPriceFilter,
        maxPriceFilter,
        minRatingFilter,
        inStockOnlyFilter
    ) { params ->
        val list = params[0] as List<ProductEntity>
        val query = (params[1] as String).trim().lowercase()
        val cat = params[2] as String
        val sort = params[3] as SortOption
        val minP = params[4] as Float
        val maxP = params[5] as Float
        val minR = params[6] as Float
        val inStock = params[7] as Boolean

        var result = list.filter { p ->
            val matchQuery = query.isEmpty() ||
                p.name.lowercase().contains(query) ||
                p.category.lowercase().contains(query) ||
                p.description.lowercase().contains(query)
            val matchCat = cat == "All" || p.category.equals(cat, ignoreCase = true)
            val matchPrice = p.price >= minP && p.price <= maxP
            val matchRating = p.rating >= minR
            val matchStock = !inStock || p.stock > 0

            matchQuery && matchCat && matchPrice && matchRating && matchStock
        }

        when (sort) {
            SortOption.RECOMMENDED -> result.sortedByDescending { if (it.isRecommended) 2 else if (it.isTrending) 1 else 0 }
            SortOption.PRICE_LOW_TO_HIGH -> result.sortedBy { it.price }
            SortOption.PRICE_HIGH_TO_LOW -> result.sortedByDescending { it.price }
            SortOption.RATING -> result.sortedByDescending { it.rating }
            SortOption.NEWEST -> result.sortedByDescending { if (it.isNewArrival) 1 else 0 }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart calculations
    val cartSubtotal: StateFlow<Double> = cartItems.combine(appliedCoupon) { items, _ ->
        items.sumOf { it.product.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartDiscount: StateFlow<Double> = combine(cartSubtotal, appliedCoupon) { subtotal, coupon ->
        when (coupon?.uppercase()) {
            "NEXCART10" -> subtotal * 0.10
            "WELCOME20" -> subtotal * 0.20
            else -> 0.0
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartShipping: StateFlow<Double> = cartSubtotal.combine(cartItems) { subtotal, items ->
        if (items.isEmpty() || subtotal >= 499.0) 0.0 else 49.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTotal: StateFlow<Double> = combine(cartSubtotal, cartDiscount, cartShipping) { sub, disc, ship ->
        (sub - disc + ship).coerceAtLeast(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Navigation Methods
    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun openProductDetail(productId: Long) {
        _selectedProductId.value = productId
        navigateTo(AppScreen.PRODUCT_DETAIL)
    }

    fun openOrderDetail(orderId: String) {
        _selectedOrderId.value = orderId
        navigateTo(AppScreen.ORDER_DETAIL)
    }

    fun openCategory(categoryName: String) {
        selectedCategoryFilter.value = categoryName
        navigateTo(AppScreen.CATEGORIES)
    }

    fun openSearch(initialQuery: String = "") {
        searchQuery.value = initialQuery
        navigateTo(AppScreen.SEARCH)
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            val prev = screenStack.last()
            _currentScreen.value = prev
            return true
        }
        return false
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }

    // Cart Actions
    fun addToCart(product: ProductEntity, quantity: Int = 1) {
        viewModelScope.launch {
            val existing = cartItems.value.firstOrNull { it.product.id == product.id }
            val currentQty = existing?.quantity ?: 0
            val newQty = currentQty + quantity
            if (newQty > product.stock) {
                showToast("Only ${product.stock} items available in stock")
                return@launch
            }
            repository.addToCart(product.id, newQty)
            showToast("Added ${product.name} to cart")
        }
    }

    fun updateCartQuantity(productId: Long, newQuantity: Int) {
        viewModelScope.launch {
            val item = cartItems.value.firstOrNull { it.product.id == productId }
            if (item != null && newQuantity > item.product.stock) {
                showToast("Cannot exceed available stock of ${item.product.stock}")
                return@launch
            }
            repository.updateCartQuantity(productId, newQuantity)
        }
    }

    fun removeFromCart(productId: Long, productName: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
            showToast("Removed $productName from cart")
        }
    }

    fun applyCoupon(code: String): Boolean {
        val trimmed = code.trim().uppercase()
        return if (trimmed == "NEXCART10" || trimmed == "WELCOME20") {
            appliedCoupon.value = trimmed
            showToast("Coupon '$trimmed' applied successfully!")
            true
        } else {
            showToast("Invalid coupon code")
            false
        }
    }

    fun removeCoupon() {
        appliedCoupon.value = null
        showToast("Coupon removed")
    }

    // Wishlist Actions
    fun toggleWishlist(product: ProductEntity) {
        viewModelScope.launch {
            val isWish = wishlistProducts.value.any { it.id == product.id }
            repository.toggleWishlist(product.id, isWish)
            if (isWish) {
                showToast("Removed from wishlist")
            } else {
                showToast("Added to wishlist")
            }
        }
    }

    fun isProductWishlisted(productId: Long): Boolean {
        return wishlistProducts.value.any { it.id == productId }
    }

    // Checkout & Order Placement
    fun placeOrder(
        customerName: String,
        customerEmail: String,
        customerPhone: String,
        address: String,
        city: String,
        state: String,
        pinCode: String,
        paymentMethod: String
    ): String {
        val currentCart = cartItems.value
        if (currentCart.isEmpty()) {
            showToast("Your cart is empty")
            return ""
        }

        val orderNum = "NC-" + (10000..99999).random()
        val itemsSummary = currentCart.joinToString(", ") { "${it.quantity}x ${it.product.name}" }

        val newOrder = OrderEntity(
            orderId = orderNum,
            customerName = customerName.ifBlank { "Guest Shopper" },
            customerEmail = customerEmail.ifBlank { "customer@example.com" },
            customerPhone = customerPhone.ifBlank { "+1 555-0199" },
            address = address.ifBlank { "123 Market St" },
            city = city.ifBlank { "New York" },
            state = state.ifBlank { "NY" },
            pinCode = pinCode.ifBlank { "10001" },
            paymentMethod = paymentMethod,
            subtotal = cartSubtotal.value,
            shipping = cartShipping.value,
            discount = cartDiscount.value,
            total = cartTotal.value,
            date = System.currentTimeMillis(),
            status = "Pending",
            itemsSummary = itemsSummary
        )

        viewModelScope.launch {
            repository.placeOrder(newOrder)
            appliedCoupon.value = null
            showToast("Order placed successfully! ID: $orderNum")
            openOrderDetail(orderNum)
        }
        return orderNum
    }

    // Admin Actions
    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
            showToast("Order $orderId status changed to $status")
        }
    }

    fun saveProduct(product: ProductEntity, isNew: Boolean) {
        viewModelScope.launch {
            if (isNew) {
                repository.addProduct(product)
                showToast("Product '${product.name}' added successfully")
            } else {
                repository.updateProduct(product)
                showToast("Product '${product.name}' updated")
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product.id)
            showToast("Product '${product.name}' deleted")
        }
    }

    fun addCategory(name: String) {
        if (name.isBlank()) return
        val id = name.trim().lowercase().replace("\\s+".toRegex(), "_")
        viewModelScope.launch {
            repository.addCategory(CategoryEntity(id = id, name = name.trim(), iconName = "category"))
            showToast("Category '$name' created")
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category.id)
            showToast("Category '${category.name}' deleted")
        }
    }

    // User Profile
    fun saveUserProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.updateUserProfile(profile)
            showToast("Profile updated successfully")
        }
    }

    fun setLoginStatus(isLoggedIn: Boolean) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.updateUserProfile(current.copy(isLoggedIn = isLoggedIn))
            showToast(if (isLoggedIn) "Logged in as ${current.name}" else "Logged out")
        }
    }

    // Settings
    fun setThemeMode(mode: String) {
        repository.setThemeMode(mode)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        repository.setNotificationsEnabled(enabled)
    }

    fun setCurrency(currency: String) {
        repository.setCurrency(currency)
    }

    fun formatPrice(amount: Double): String {
        val curr = selectedCurrency.value
        val symbol = when (curr) {
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            else -> "₹"
        }
        return if (curr == "INR") {
            if (amount % 1.0 == 0.0) {
                String.format(Locale("en", "IN"), "%s%,.0f", symbol, amount)
            } else {
                String.format(Locale("en", "IN"), "%s%,.2f", symbol, amount)
            }
        } else {
            String.format(Locale.US, "%s%.2f", symbol, amount)
        }
    }

    fun clearFilters() {
        selectedCategoryFilter.value = "All"
        searchQuery.value = ""
        minPriceFilter.value = 0f
        maxPriceFilter.value = 150000f
        minRatingFilter.value = 0f
        inStockOnlyFilter.value = false
        selectedSortOption.value = SortOption.RECOMMENDED
    }
}
