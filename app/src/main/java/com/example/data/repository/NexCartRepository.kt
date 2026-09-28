package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppDatabase
import com.example.data.local.DemoData
import com.example.data.model.CartItemEntity
import com.example.data.model.CartItemWithProduct
import com.example.data.model.CategoryEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WishlistItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NexCartRepository(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val database = AppDatabase.getDatabase(context, scope)
    private val productDao = database.productDao()
    private val cartDao = database.cartDao()
    private val wishlistDao = database.wishlistDao()
    private val categoryDao = database.categoryDao()
    private val orderDao = database.orderDao()
    private val userDao = database.userDao()

    private val prefs: SharedPreferences =
        context.getSharedPreferences("nexcart_preferences", Context.MODE_PRIVATE)

    // Settings StateFlows
    private val _themeMode = MutableStateFlow(prefs.getString("pref_theme_mode", "system") ?: "system")
    val themeMode = _themeMode.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean("pref_notifications", true))
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

    private val _selectedCurrency = MutableStateFlow(prefs.getString("pref_currency", "INR") ?: "INR")
    val selectedCurrency = _selectedCurrency.asStateFlow()

    init {
        // Ensure data is seeded immediately on launch
        scope.launch(Dispatchers.IO) {
            checkAndSeedData()
        }
    }

    private suspend fun checkAndSeedData() {
        val dataVersion = prefs.getInt("pref_data_version", 1)
        val productCount = productDao.getProductCount()
        if (productCount == 0 || dataVersion < 2) {
            productDao.insertProducts(DemoData.products)
            categoryDao.insertCategories(DemoData.categories)
            if (productCount == 0 || dataVersion < 2) {
                for (order in DemoData.demoOrders) {
                    orderDao.insertOrder(order)
                }
            }
            if (productCount == 0) {
                userDao.insertOrUpdateUser(UserProfileEntity())
            }
            prefs.edit()
                .putInt("pref_data_version", 2)
                .putString("pref_currency", "INR")
                .apply()
            _selectedCurrency.value = "INR"
        }
    }

    // Products
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val activeProducts: Flow<List<ProductEntity>> = productDao.getActiveProducts()

    fun getProductById(id: Long): Flow<ProductEntity?> = productDao.getProductById(id)

    suspend fun addProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(id: Long) = withContext(Dispatchers.IO) {
        productDao.deleteProductById(id)
        cartDao.deleteItem(id)
        wishlistDao.removeFromWishlist(id)
    }

    // Cart
    val cartItemsWithProducts: Flow<List<CartItemWithProduct>> =
        combine(cartDao.getCartItems(), productDao.getAllProducts()) { cartItems, allProducts ->
            val productMap = allProducts.associateBy { it.id }
            cartItems.mapNotNull { cartItem ->
                productMap[cartItem.productId]?.let { product ->
                    CartItemWithProduct(product = product, quantity = cartItem.quantity)
                }
            }
        }

    suspend fun addToCart(productId: Long, quantity: Int = 1) = withContext(Dispatchers.IO) {
        val existing = cartDao.getCartItems()
        // Simple insert or update
        cartDao.insertOrUpdate(CartItemEntity(productId = productId, quantity = quantity))
    }

    suspend fun updateCartQuantity(productId: Long, newQuantity: Int) = withContext(Dispatchers.IO) {
        if (newQuantity <= 0) {
            cartDao.deleteItem(productId)
        } else {
            cartDao.insertOrUpdate(CartItemEntity(productId = productId, quantity = newQuantity))
        }
    }

    suspend fun removeFromCart(productId: Long) = withContext(Dispatchers.IO) {
        cartDao.deleteItem(productId)
    }

    suspend fun clearCart() = withContext(Dispatchers.IO) {
        cartDao.clearCart()
    }

    // Wishlist
    val wishlistProducts: Flow<List<ProductEntity>> =
        combine(wishlistDao.getWishlistItems(), productDao.getAllProducts()) { wishItems, allProducts ->
            val wishIds = wishItems.map { it.productId }.toSet()
            allProducts.filter { wishIds.contains(it.id) }
        }

    fun isInWishlist(productId: Long): Flow<Boolean> = wishlistDao.isInWishlist(productId)

    suspend fun toggleWishlist(productId: Long, currentlyWishlisted: Boolean) = withContext(Dispatchers.IO) {
        if (currentlyWishlisted) {
            wishlistDao.removeFromWishlist(productId)
        } else {
            wishlistDao.addToWishlist(WishlistItemEntity(productId = productId))
        }
    }

    // Categories
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    suspend fun addCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        categoryDao.insertCategory(category)
    }

    suspend fun deleteCategory(id: String) = withContext(Dispatchers.IO) {
        categoryDao.deleteCategory(id)
    }

    // Orders
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()

    fun getOrderById(orderId: String): Flow<OrderEntity?> = orderDao.getOrderById(orderId)

    suspend fun placeOrder(order: OrderEntity) = withContext(Dispatchers.IO) {
        orderDao.insertOrder(order)
        cartDao.clearCart()
    }

    suspend fun updateOrderStatus(orderId: String, status: String) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(orderId, status)
    }

    // User Profile
    val userProfile: Flow<UserProfileEntity?> = userDao.getUserProfile()

    suspend fun updateUserProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        userDao.insertOrUpdateUser(profile)
    }

    // Settings
    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        prefs.edit().putString("pref_theme_mode", mode).apply()
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        prefs.edit().putBoolean("pref_notifications", enabled).apply()
    }

    fun setCurrency(currency: String) {
        _selectedCurrency.value = currency
        prefs.edit().putString("pref_currency", currency).apply()
    }
}
