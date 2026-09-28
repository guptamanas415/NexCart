package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val category: String,
    val description: String,
    val price: Double,
    val oldPrice: Double,
    val discountPercent: Int,
    val rating: Float,
    val ratingCount: Int,
    val stock: Int,
    val imageUrl: String,
    val badge: String,
    val isTrending: Boolean,
    val isBestSeller: Boolean,
    val isNewArrival: Boolean,
    val isRecommended: Boolean,
    val isActive: Boolean = true
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: Long,
    val quantity: Int
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey val productId: Long,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String,
    val address: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val paymentMethod: String,
    val subtotal: Double,
    val shipping: Double,
    val discount: Double,
    val total: Double,
    val date: Long,
    val status: String,
    val itemsSummary: String // JSON-like formatted items summary
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Alex Morgan",
    val email: String = "alex.morgan@example.com",
    val phone: String = "+1 (555) 234-5678",
    val address: String = "742 Evergreen Terrace",
    val city: String = "Springfield",
    val state: String = "OR",
    val pinCode: String = "97477",
    val isLoggedIn: Boolean = true
)

enum class OrderStatus(val label: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    PACKED("Packed"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    companion object {
        fun fromString(value: String): OrderStatus {
            return entries.firstOrNull { it.label.equals(value, ignoreCase = true) || it.name.equals(value, ignoreCase = true) }
                ?: PENDING
        }
    }
}

data class CartItemWithProduct(
    val product: ProductEntity,
    val quantity: Int
)

data class OrderItemSummary(
    val productId: Long,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String
)

data class DemoCustomer(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val orderCount: Int,
    val registeredDate: String
)
