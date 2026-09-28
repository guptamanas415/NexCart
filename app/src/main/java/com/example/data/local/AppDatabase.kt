package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WishlistItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        CartItemEntity::class,
        WishlistItemEntity::class,
        CategoryEntity::class,
        OrderEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun categoryDao(): CategoryDao
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nexcart_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        scope.launch(Dispatchers.IO) {
                            INSTANCE?.let { database ->
                                database.productDao().insertProducts(DemoData.products)
                                database.categoryDao().insertCategories(DemoData.categories)
                                for (order in DemoData.demoOrders) {
                                    database.orderDao().insertOrder(order)
                                }
                                database.userDao().insertOrUpdateUser(UserProfileEntity())
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
